package com.github.droidbaza.securepreference

import kotlinx.coroutines.flow.Flow


interface SecurePreference {

    /**
     * Returns a Flow that emits key names when data is changed in the storage.
     * Useful for observing updates in real-time.
     *
     * @return Flow emitting updated key names (add or remove values for key).
     */
    fun keys(lastTrigger: Boolean = true): Flow<String>

    /**
     * Observes changes for a specific key and emits the latest value.
     *
     * @param keyName The key to observe.
     * @param default The default value to emit if the key is not found.
     * @return Flow emitting the latest value associated with the key.
     */
    fun <T : Any> keyResult(keyName: String, default: T?): Flow<T?>

    /**
     * Stores a value in encrypted shared preferences.
     *
     * Supported types: Boolean, Int, String, Float, Long, Double, and Set<String>.
     *
     * @param key The key under which the value is stored.
     * @param value The value to store.
     */
    fun <T : Any> put(key: String, value: T)

    /**
     * Stores multiple key-value pairs in encrypted shared preferences.
     *
     * Supported types: Boolean, Int, String, Float, Long, Double, and Set<String>.
     *
     * @param pairs Key-value pairs to store.
     */
    fun <T : Any> put(vararg pairs: Pair<String, T>)

    /**
     * Retrieves a value from encrypted shared preferences.
     *
     * @param key The key to retrieve the value from.
     * @param defaultValue The default value if the key does not exist.
     * @return The stored value or the default value if the key is missing.
     */
    fun <T> get(key: String, defaultValue: T? = null): T?

    /**
     * Removes a value associated with the given key.
     *
     * @param key The key to remove from storage.
     */
    fun clear(key: String)

    /**
     * Removes multiple values associated with the given keys.
     *
     * @param keys The keys to remove from storage.
     */
    fun clear(vararg keys: String)

    /**
     * Removes multiple values associated with the given collection of keys.
     * If an empty collection is provided, all stored values will be removed.
     *
     * @param keys The collection of keys to remove from storage.
     */
    fun clear(keys: Collection<String> = emptyList())

    /**
     * Observes changes for count of records
     */
    fun count(): Flow<Int>

    /**
     * Count of records
     */
    val count: Int
}
