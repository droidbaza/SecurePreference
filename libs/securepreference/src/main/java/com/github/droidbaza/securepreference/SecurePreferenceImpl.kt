package com.github.droidbaza.securepreference

import android.content.Context
import android.content.SharedPreferences
import android.os.Parcel
import android.os.Parcelable
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KClass


open class SecurePreferenceImpl(
    applicationContext: Context,
    keyStoreAlias: String = "keyStoreAlias",
    prefsFileKeyName: String = "prefsFileName",
    encryptionPaddings: String = KeyProperties.ENCRYPTION_PADDING_NONE,
    blockModes: String = KeyProperties.BLOCK_MODE_GCM,
    keySize: Int = 256,
    private val coroutineContext: CoroutineContext = Dispatchers.IO
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
    }.flowOn(coroutineContext)

    override fun <T : Any> keyResult(keyName: String, default: T): Flow<T?> = callbackFlow {
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
    }.flowOn(coroutineContext)

    override fun <T : Any> put(key: String, value: T) {
        val editor = sp.edit()
        val type = value::class

        when (value) {
            is Boolean -> editor.putBoolean(key, value)
            is Int -> editor.putInt(key, value)
            is String -> editor.putString(key, value)
            is Float -> editor.putFloat(key, value)
            is Long -> editor.putLong(key, value)
            is Double -> editor.putString(key, value.toString())
            is Set<*> -> editor.putStringSet(key, value.mapNotNull { it?.toString() }.toSet())
            is Parcelable -> putParcelable(key, value)
            else -> {
                try {
                    editor.putString(key, encode(value, type as KClass<T>))
                } catch (ex: Exception) {
                    throw Throwable("Current type$type is not supported ${ex.message} ")
                }
            }
        }
        editor.apply()
    }

    override suspend fun <T : Any> putAsync(key: String, value: T) {
        return withContext(coroutineContext) {
            put(key, value)
        }
    }

    @OptIn(InternalSerializationApi::class)
    @Suppress("Unchecked_cast")
    override fun <T : Any> get(key: String, defaultValue: T): T {
        return with(sp) {
            when (defaultValue) {
                is Boolean -> getBoolean(key, defaultValue) as T
                is Int -> getInt(key, defaultValue) as T
                is String -> getString(key, defaultValue) as T
                is Float -> getFloat(key, defaultValue) as T
                is Long -> getLong(key, defaultValue as Long) as T
                is Double -> getString(key, "$defaultValue")?.toDoubleOrNull() as T
                is Set<*> -> getStringSet(key, emptySet()) as T
                is Parcelable -> getParcelable(key, defaultValue::class.java) as T
                else -> decode(getString(key, ""), defaultValue::class) as T
            }
        }
    }


    override suspend fun <T : Any> getAsync(key: String, defaultValue: T): T {
        return withContext(coroutineContext) {
            get(key, defaultValue)
        }
    }

    override fun <T : Any> put(vararg pairs: Pair<String, T>) {
        pairs.forEach {
            put(it.first, it.second)
        }
    }

    override suspend fun <T : Any> putAsync(vararg pairs: Pair<String, T>) {
        withContext(coroutineContext) {
            put(*pairs)
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
        get() = keys.size

    override fun count(): Flow<Int> = callbackFlow {
        send(count)
        val callback = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            trySend(count)
        }
        sp.registerOnSharedPreferenceChangeListener(callback)
        awaitClose {
            sp.unregisterOnSharedPreferenceChangeListener(callback)
        }
    }.flowOn(coroutineContext)

    override val keys: Collection<String>
        get() = sp.all.keys

    override val keyValues: Map<String, *>
        get() = sp.all

    @OptIn(InternalSerializationApi::class)
    private fun <T : Any> encode(
        value: T,
        clazz: KClass<T>,
        json: Json = Json
    ): String {
        return json.encodeToString(clazz.serializer(), value)
    }

    @OptIn(InternalSerializationApi::class)
    private fun <T : Any> decode(
        value: String?,
        clazz: KClass<T>,
        json: Json = Json
    ): T? {
        value ?: return null
        return json.decodeFromString(clazz.serializer(), value)
    }


    private fun putParcelable(key: String, value: Parcelable) {
        val parcel = Parcel.obtain()
        value.writeToParcel(parcel, 0)
        val byteArray = parcel.marshall()
        val result = Base64.encodeToString(byteArray, Base64.DEFAULT)
        parcel.recycle()
        put(key, result)
    }

    private fun <T : Any> getParcelable(key: String, clazz: Class<T>): Any? {
        val base64String = get(key, "").ifEmpty { return null }
        val bytes = Base64.decode(base64String, Base64.DEFAULT)
        val parcel = Parcel.obtain()
        parcel.unmarshall(bytes, 0, bytes.size)
        parcel.setDataPosition(0)
        val creatorField = clazz.getField("CREATOR")
        val creator = creatorField.get(null) as Parcelable.Creator<*>
        val result = creator.createFromParcel(parcel)
        parcel.recycle()
        return result
    }
}
