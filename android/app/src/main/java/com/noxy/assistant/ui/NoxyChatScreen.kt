package com.noxy.assistant.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.noxy.assistant.viewmodel.NoxyChatViewModel

@Composable
fun NoxyChatScreen(viewModel: NoxyChatViewModel) {
    val messages by viewModel.chatMessages.collectAsState()
    val debugInfo by viewModel.debugInfo.collectAsState()

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        messages.forEach { message ->
            Text(text = message, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Divider()
        debugInfo?.let { info ->
            Spacer(modifier = Modifier.height(8.dp))
            info.personalityStyle?.let { style ->
                Text(text = "Style de Noxy : $style", style = MaterialTheme.typography.bodyMedium)
            }
            info.usedMemorySummary?.let { memory ->
                Text(text = "Basé sur ta mémoire :\n$memory", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
