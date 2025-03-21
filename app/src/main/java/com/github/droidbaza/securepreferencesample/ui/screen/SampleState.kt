package com.github.droidbaza.securepreferencesample.ui.screen

data class SampleState(
    val count: Int = 0,
    val targetValue: Any? = null,
    val targetKey: String = "",
    val data: List<Pair<String, Any>> = listOf(
        "Int" to 0,
        "String" to "",
        "Boolean" to false,
        "Float" to 0f,
        "Long" to 0L,
        "Double" to 0.0,
        "Set<String>" to setOf("")
    )
)
