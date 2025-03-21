package com.github.droidbaza.securepreference

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn

open class SecurePreferenceImpl(
    applicationContext: Context,
    keyStoreAlias: String = "keyStoreAlias",
    prefsFileKeyName: String = "prefsFileName",
    encryptionPaddings: String = KeyProperties.ENCRYPTION_PADDING_NONE,
    blockModes: String = KeyProperties.BLOCK_MODE_GCM,
    keySize: Int = 256,
    private val coroutineContext: CoroutineDispatcher = Dispatchers.IO,
) : SecurePreference {

    open val sp: SharedPreferences by lazy {
        val keyGenParameterSpec =
            KeyGenParameterSpec.Builder(
                keyStoreAlias,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            ).setBlockModes(blockModes)
                .setEncryptionPaddings(encryptionPaddings)
                .setKeySize(keySize)
                .build()
        val masterKeyAlias = MasterKey.Builder(applicationContext, keyStoreAlias)
            .setKeyGenParameterSpec(keyGenParameterSpec)
            .build()
        EncryptedSharedPreferences.create(
            applicationContext,
            prefsFileKeyName,
            masterKeyAlias,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    override fun keys(lastTrigger: Boolean): Flow<String> = callbackFlow {
        if (lastTrigger) {
            sp.all.keys.lastOrNull()?.let {
                send(it)
            }
        }
        val callback = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            key?.let {
                trySend(it)
            }
        }
        sp.registerOnSharedPreferenceChangeListener(callback)
        awaitClose {
            sp.unregisterOnSharedPreferenceChangeListener(callback)
        }
    }.flowOn(coroutineContext).catch {}

    override fun <T : Any> keyResult(keyName: String, default: T?): Flow<T?> = callbackFlow {
        send(get(keyName, default))
        val callback = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (keyName == key) {
                trySend(get(key, default))
            }
        }
        sp.registerOnSharedPreferenceChangeListener(callback)
        awaitClose {
            sp.unregisterOnSharedPreferenceChangeListener(callback)
        }
    }.flowOn(coroutineContext).catch {}

    override fun <T : Any> put(key: String, value: T) {
        val editor = sp.edit()
        when (value) {
            is Boolean -> editor.putBoolean(key, value)
            is Int -> editor.putInt(key, value)
            is String -> editor.putString(key, value)
            is Float -> editor.putFloat(key, value)
            is Long -> editor.putLong(key, value)
            is Double -> editor.putString(key, value.toString())
            is Set<*> -> editor.putStringSet(key, value.mapNotNull { it?.toString() }.toSet())
        }
        editor.apply()
    }

    @Suppress("Unchecked_cast")
    override fun <T> get(key: String, defaultValue: T): T {
        return when (defaultValue) {
            is Boolean -> sp.getBoolean(key, defaultValue) as T
            is Int -> sp.getInt(key, defaultValue) as T
            is String -> sp.getString(key, defaultValue) as T
            is Float -> sp.getFloat(key, defaultValue) as T
            is Long -> sp.getLong(key, defaultValue as Long) as T
            is Double -> sp.getString(key, "$defaultValue")?.toDoubleOrNull() as T
            is Set<*> -> sp.getStringSet(key, emptySet()) as T
            else -> throw Throwable("Current type:${defaultValue!!::class} not supported")
        }
    }

    override fun <T : Any> put(vararg pairs: Pair<String, T>) {
        pairs.forEach {
            put(it.first, it.second)
        }
    }

    override fun clear(key: String?) {
        if (key != null) {
            sp.edit().remove(key).apply()
        } else sp.edit().clear().apply()
    }

    override fun clear(vararg keys: String) {
        keys.forEach {
            clear(key = it)
        }
        if (keys.isEmpty()) {
            clear(key = null)
        }
    }

    override fun clear(keys: Collection<String>) {
        keys.forEach {
            clear(key = it)
        }
        if (keys.isEmpty()) {
            clear(key = null)
        }
    }

    override val count: Int
        get() = sp.all.keys.size

    override fun count(): Flow<Int> = callbackFlow {
        send(count)
        val callback = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            trySend(count)
        }
        sp.registerOnSharedPreferenceChangeListener(callback)
        awaitClose {
            sp.unregisterOnSharedPreferenceChangeListener(callback)
        }
    }.flowOn(coroutineContext).catch {}
}
