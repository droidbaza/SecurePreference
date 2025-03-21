package com.github.droidbaza.securepreferencesample.ui.screen

sealed interface SampleAction {
    data class GenerateAndSave(val item: Pair<String, Any>) : SampleAction
    data class Remove(val item: Pair<String, Any>?=null) : SampleAction
    data class Find(val item: Pair<String, Any>) : SampleAction
}
