package com.github.droidbaza.securepreference

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import kotlin.reflect.KClass

interface SecurePreference {

    /**
     * Returns a flow of all the keys stored in the preferences.
     *
     * @param lastTrigger - A flag that triggers the last emitted key. Default is `true`.
     * @return Flow<String> - Flow emitting the keys.
     */
    fun keys(lastTrigger: Boolean = true): Flow<String>

    /**
     * Retrieves the value associated with the specified key.
     *
     * @param keyName - The key whose value is to be retrieved.
     * @param default - The default value to return if the key is not found.
     * @return Flow<T?> - Flow emitting the value for the key, or `null` if not found.
     * @param T - Supported types: Boolean, Int, String, Float, Long, Double, Set<String>, etc.
     */
    fun <T : Any> keyResult(keyName: String, default: T): Flow<T?>

    /**
     * Saves a key-value pair in the preferences.
     *
     * @param key - The key to associate with the value.
     * @param value - The value to be stored.
     * @param T - Supported types: Boolean, Int, String, Float, Long, Double, Set<String>,Parcellable,Serialazible etc.
     */
    fun <T : Any> put(key: String, value: T)

    /**
     * Saves a key-value pair asynchronously in the preferences.
     *
     * @param key - The key to associate with the value.
     * @param value - The value to be stored.
     * @param T - Supported types: Boolean, Int, String, Float, Long, Double, Set<String>, etc.
     */
    suspend fun <T : Any> putAsync(key: String, value: T)

    /**
     * Saves multiple key-value pairs in the preferences.
     *
     * @param pairs - Vararg of pairs (key, value) to be stored.
     * @param T - Supported types: Boolean, Int, String, Float, Long, Double, Set<String>, etc.
     */
    fun <T : Any> put(vararg pairs: Pair<String, T>)

    /**
     * Saves multiple key-value pairs asynchronously in the preferences.
     *
     * @param pairs - Vararg of pairs (key, value) to be stored.
     * @param T - Supported types: Boolean, Int, String, Float, Long, Double, Set<String>, etc.
     */
    suspend fun <T : Any> putAsync(vararg pairs: Pair<String, T>)

    /**
     * Retrieves the value associated with the specified key.
     *
     * @param key - The key whose value is to be retrieved.
     * @param defaultValue - The default value to return if the key is not found.
     * @return T - The value associated with the key, or the default value if not found.
     * @param T - Supported types: Boolean, Int, String, Float, Long, Double, Set<String>, etc.
     */
    fun <T : Any> get(key: String, defaultValue: T): T

    /**
     * Retrieves the value associated with the specified key asynchronously.
     *
     * @param key - The key whose value is to be retrieved.
     * @param defaultValue - The default value to return if the key is not found.
     * @return T - The value associated with the key, or the default value if not found.
     * @param T - Supported types: Boolean, Int, String, Float, Long, Double, Set<String>, etc.
     */
    suspend fun <T : Any> getAsync(key: String, defaultValue: T): T

    /**
     * Clears the value associated with the specified key.
     *
     * @param key - The key whose value is to be cleared. If `null`, all keys are cleared.
     */
    fun clear(key: String? = null)

    /**
     * Clears the values associated with the specified keys.
     *
     * @param keys - Vararg of keys to be cleared.
     */
    fun clear(vararg keys: String)

    /**
     * Clears the values associated with the specified keys.
     *
     * @param keys - A collection of keys to be cleared. Defaults to an empty list.
     */
    fun clear(keys: Collection<String> = emptyList())

    /**
     * Returns a flow of the total count of stored key-value pairs.
     *
     * @return Flow<Int> - Flow emitting the number of stored key-value pairs.
     */
    fun count(): Flow<Int>

    /**
     * Returns the total count of stored key-value pairs.
     *
     * @return Int - The number of stored key-value pairs.
     */
    val count: Int

    /**
     * Returns a collection of all the keys stored in the preferences.
     *
     * @return Collection<String> - A collection of keys.
     */
    val keys: Collection<String>

    /**
     * Returns a map of all stored key-value pairs.
     *
     * @return Map<String, *> - A map of key-value pairs, where values can be of any supported type.
     */
    val keyValues: Map<String, *>
}
