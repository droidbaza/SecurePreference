package com.github.droidbaza.securepreferencesample.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.droidbaza.securepreferencesample.ui.screen.SampleAction

@Composable
fun DataTypeCard(
    item: Pair<String, Any>,
    action: (SampleAction) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Тип данных: ${item.first}")
            Spacer(Modifier.padding(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = { action(SampleAction.Find(item)) }) {
                    Text("FIND")
                }
                Button(onClick = { action(SampleAction.Remove(item)) }) {
                    Text("DEL")
                }
                Button(onClick = { action(SampleAction.GenerateAndSave(item)) }) {
                    Text("GEN AND SAVE")
                }
            }
        }
    }
}