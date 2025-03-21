package com.github.droidbaza.securepreference

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.runner.RunWith
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class SecurePreferenceTest {

    private lateinit var context: Context
    private lateinit var securePreference: SecurePreference
    private val testDispatcher = StandardTestDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        context = ApplicationProvider.getApplicationContext()
        securePreference = SecurePreferenceImpl(context)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testPutAndGetBoolean() {
        val key = "testBoolean"
        val value = true
        securePreference.put(key, value)
        val retrievedValue: Boolean? = securePreference.get(key, false)
        assertEquals(value, retrievedValue)
    }

    @Test
    fun testPutAndGetInt() {
        val key = "testInt"
        val value = 123
        securePreference.put(key, value)
        val retrievedValue: Int? = securePreference.get(key, 0)
        assertEquals(value, retrievedValue)
    }

    @Test
    fun testPutAndGetString() {
        val key = "testString"
        val value = "Hello, world!"
        securePreference.put(key, value)
        val retrievedValue = securePreference.get(key, "")
        assertEquals(value, retrievedValue)
    }

    @Test
    fun testPutAndGetSetString() {
        val key = "testSetString"
        val value = setOf("1", "2", "3")
        securePreference.put(key, value)
        val retrievedValue = securePreference.get(key, setOf<String>())
        assertEquals(value, retrievedValue)
    }

    @Test
    fun testPutAndGetFloat() {
        val key = "testFloat"
        val value = 3.14f
        securePreference.put(key, value)
        val retrievedValue: Float? = securePreference.get(key, 0f)
        assertEquals(value, retrievedValue)
    }

    @Test
    fun testPutAndGetLong() = runTest {
        val key = "testLong"
        val value = 1234567890L
        securePreference.put(key, value)
        val retrievedValue: Long? = securePreference.get(key, 0L)
        var savedKey: String? = null
        var savedValue: Long? = null

        val job1 = launch {
            securePreference.keys().take(1).collectLatest {
                savedKey = it
            }
        }
        val job2 = launch {
            securePreference.keyResult(key, 0L).take(1).collect {
                savedValue = it
            }
        }
        joinAll(job1,job2)

        assertEquals(value, retrievedValue,"start")
        assertEquals(key, savedKey,"from key")
        assertEquals(value, savedValue,"from flow")
    }

    @Test
    fun testPutAndGetDouble() {
        val key = "testDouble"
        val value = 3.1415926535
        securePreference.put(key, value)
        val retrievedValue: Double? = securePreference.get(key, 0.0)
        assertEquals(value, retrievedValue)
    }

    @Test
    fun testPutMultipleValues() {
        val pairs = arrayOf(
            "testBoolean" to true,
            "testInt" to 123,
            "testString" to "Hello, world!"
        )
        securePreference.put(*pairs)
        assertEquals(true, securePreference.get("testBoolean", false))
        assertEquals(123, securePreference.get("testInt", 0))
        assertEquals("Hello, world!", securePreference.get("testString", ""))
    }

    @Test
    fun testClearKey() {
        val key = "testKey"
        securePreference.put(key, "value")
        securePreference.clear(key)
        val retrievedValue = securePreference.get(key,"")
        assertEquals("", retrievedValue)
    }
}
