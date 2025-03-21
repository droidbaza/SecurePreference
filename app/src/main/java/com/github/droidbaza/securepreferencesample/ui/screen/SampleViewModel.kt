package com.github.droidbaza.securepreferencesample.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.droidbaza.securepreference.SecurePreference
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.random.Random

class SampleViewModel(private val sp: SecurePreference) : ViewModel() {

    private val _state = MutableStateFlow(SampleState())
    val state: StateFlow<SampleState> = _state
    private val random = Random(System.currentTimeMillis())
    private var job: Job? = null

    init {
        loadData()
    }

    fun onAction(action: SampleAction) {
        when (action) {
            is SampleAction.GenerateAndSave -> generateAndSaveData(action.item)
            is SampleAction.Find -> findData(action.item)
            is SampleAction.Remove -> removeData(action.item?.first)
        }
    }

    private val randomString
        get() = UUID.randomUUID().toString().take(8)

    private fun generateAndSaveData(item: Pair<String, Any>) {
        val key = item.first
        val value = when (key) {
            "Int" -> random.nextInt(100)
            "String" -> randomString
            "Boolean" -> random.nextBoolean()
            "Float" -> random.nextFloat()
            "Double" -> random.nextDouble()
            "Long" -> random.nextLong()
            "Set<String>" -> setOf(randomString, randomString, randomString)
            else -> null
        } ?: return
        sp.put(key, value)
    }

    private fun removeData(key: String?) {
        sp.clear(key)
        if (key == null) {
            loadData()
        }
    }

    private fun loadData() {
        job?.cancel()
        job = viewModelScope.launch {
            sp.count().collectLatest { count ->
                _state.update {
                    it.copy(count = count)
                }
            }
        }
    }

    private fun findData(item: Pair<String, Any>) {
        val key = item.first
        val default = item.second
        _state.update {
            it.copy(targetValue = sp.get(key, default), targetKey = key)
        }
    }
}
