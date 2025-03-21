package com.github.droidbaza.securepreferencesample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.github.droidbaza.securepreference.SecurePrefs
import com.github.droidbaza.securepreferencesample.ui.screen.SampleRoot
import com.github.droidbaza.securepreferencesample.ui.screen.SampleViewModel
import com.github.droidbaza.securepreferencesample.ui.theme.SecurePreferenceTheme


class MainActivity : ComponentActivity() {

    val prefs by SecurePrefs(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel = SampleViewModel(prefs)
        setContent {
            SecurePreferenceTheme {
                SampleRoot(viewModel)
            }
        }
    }
}






