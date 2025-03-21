package com.github.droidbaza.securepreferencesample.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.droidbaza.securepreferencesample.ui.component.DataTypeCard


@Composable
fun SampleRoot(viewModel: SampleViewModel) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    SampleScreen(state.value, viewModel::onAction)
}

@Composable
fun SampleScreen(state: SampleState, action: (SampleAction) -> Unit) {
    Column(Modifier.padding(16.dp)) {
        Text("Записей: ${state.count}")
        Spacer(Modifier.height(16.dp))
        Text("Найдено: ${state.targetValue}")
        Spacer(Modifier.height(16.dp))
        Button(onClick = { action(SampleAction.Remove()) }) {
            Text("CLEAR ALL")
        }
        Spacer(Modifier.height(16.dp))
        LazyColumn {
            items(state.data) {
                DataTypeCard(it, action)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Preview
@Composable
fun PreviewSampleScreen() {
    SampleScreen(SampleState()) { }
}