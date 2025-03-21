package com.github.droidbaza.securepreference

import android.content.Context
import android.content.SharedPreferences

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.joinAll
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.to

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [22, 28])
class SecurePreferenceTest {

    private lateinit var context: Context
    private lateinit var securePreference: SecurePreference
    private val testDispatcher = StandardTestDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        context = ApplicationProvider.getApplicationContext()
        securePreference = object :SecurePreferenceImpl(context){
            override val sp: SharedPreferences = context.getSharedPreferences("name", Context.MODE_PRIVATE)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }


    @Test
    fun testPutAndGetBoolean() = runTest {
        val key = "testBoolean"
        val value = true
        securePreference.put(key, value)
        val retrievedValue: Boolean? = securePreference.get(key, false)
        assertEquals(value, retrievedValue)
    }

    @Test
    fun testPutAndGetInt() = runTest {
        val key = "testInt"
        val value = 123
        securePreference.put(key, value)
        val retrievedValue: Int? = securePreference.get(key, 0)
        assertEquals(value, retrievedValue)
    }

    @Test
    fun testPutAndGetString() = runTest {
        val key = "testString"
        val value = "Hello, world!"
        securePreference.put(key, value)
        val retrievedValue = securePreference.get(key,"")
        assertEquals(value, retrievedValue)
    }

    @Test
    fun testPutAndGetSetString() = runTest {
        val key = "testSetString"
        val value = setOf<String>("1", "2", "3")
        securePreference.put(key, value)
        val retrievedValue = securePreference.get(key, setOf<String>())
        assertEquals(value, retrievedValue)
    }

    @Test
    fun testPutAndGetFloat() = runTest {
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
    fun testPutAndGetDouble() = runTest {
        val key = "testDouble"
        val value = 3.1415926535
        securePreference.put(key, value)
        val retrievedValue: Double? = securePreference.get(key, 0.0)
        assertEquals(value, retrievedValue)
    }

    @Test
    fun testPutMultipleValues() = runTest {
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
    fun `keysFlow emits correct keys on changes`() = runTest {
        val key1 = "key1"
        val key2 = "key2"
        val collectedKeys = mutableListOf<String>()
        val job = launch {
            securePreference.keys().take(2).collectLatest { key ->
                collectedKeys.add(key)
            }
        }
        securePreference.put(key1, "value1")
        securePreference.put(key2, "value2")
        securePreference.clear(key1)
        job.join()

        // Проверяем, что Flow выдал ожидаемые ключи
        assertEquals(listOf(key1, key2), collectedKeys)
    }


    @Test
    fun `countFlow emits correct number of record`() = runTest {
        val collectedCounts = mutableListOf<Int>()
        // Подписываемся на flow и собираем 3 значения
        val job = launch {
            securePreference.count().take(3).collectLatest {
                collectedCounts.add(it)
            }
        }
        securePreference.put("key1", "value1")
        securePreference.put("key2", "value2")
        securePreference.put("key3", "value4")
        securePreference.clear("key3")

        job.join()
        // Добавляем значения, что вызовет изменения


        assertEquals(listOf(0, 1, 2, 3, 2), collectedCounts)
    }
}