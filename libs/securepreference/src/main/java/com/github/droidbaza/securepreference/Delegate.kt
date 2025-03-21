package com.github.droidbaza.securepreference

import android.content.Context
import android.security.keystore.KeyProperties
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class SecurePrefs(
    context: Context,
    keyStoreAlias: String = "keyStoreAlias",
    prefsFileKeyName: String = "prefsFileName",
    encryptionPaddings: String = KeyProperties.ENCRYPTION_PADDING_NONE,
    blockModes: String = KeyProperties.BLOCK_MODE_GCM,
    keySize: Int = 256,
) : ReadOnlyProperty<Any, SecurePreference> {

    private val instance by lazy {
        SecurePreferenceImpl(
            context,
            keyStoreAlias,
            prefsFileKeyName,
            encryptionPaddings,
            blockModes,
            keySize
        )
    }

    override fun getValue(thisRef: Any, property: KProperty<*>): SecurePreference {
        return instance
    }
}