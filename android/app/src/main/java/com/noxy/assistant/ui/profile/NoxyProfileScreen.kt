package com.noxy.assistant.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.noxy.assistant.models.MemoryItem
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun NoxyProfileScreen(viewModel: NoxyProfileViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val formatter = rememberDateFormatter()

    LaunchedEffect(Unit) {
        viewModel.loadProfile()
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            uiState.personality?.let { personality ->
                Text(text = "Personality", style = MaterialTheme.typography.titleLarge)
                Text(text = "Style: ${personality.style}")
                Text(text = "Tone: ${personality.tone}")
                Text(text = "Traits: ${personality.traits.joinToString(", ")}")

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("cosmic_friendly", "serious_helper", "playful_companion").forEach { preset ->
                        Button(onClick = {
                            coroutineScope.launch {
                                viewModel.updatePersonality(
                                    style = preset,
                                    tone = null,
                                    traits = null
                                )
                            }
                        }) {
                            Text(text = preset)
                        }
                    }
                }
            }

            Text(text = "Memory", style = MaterialTheme.typography.titleLarge)
            MemoryList(items = uiState.memoryItems, formatter = formatter)

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = uiState.newMemoryText,
                    onValueChange = { viewModel.onNewMemoryChange(it) },
                    label = { Text("Fait à retenir") }
                )
                Button(
                    onClick = {
                        coroutineScope.launch {
                            viewModel.addMemory()
                        }
                    },
                    enabled = !uiState.isLoading
                ) {
                    Text("Ajouter à la mémoire")
                }
            }

            uiState.errorMessage?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }

            if (uiState.isLoading) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
private fun MemoryList(items: List<MemoryItem>, formatter: DateTimeFormatter) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(items) { item ->
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = item.content, style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = formatter.format(
                        Instant.ofEpochMilli((item.createdAt * 1000).toLong())
                    ),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
private fun rememberDateFormatter(): DateTimeFormatter {
    return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneId.systemDefault())
}
