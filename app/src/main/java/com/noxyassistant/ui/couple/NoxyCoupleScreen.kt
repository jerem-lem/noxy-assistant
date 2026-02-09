package com.noxyassistant.ui.couple

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.noxyassistant.models.NoxyMood

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoxyCoupleScreen(viewModel: NoxyCoupleViewModel = viewModel()) {
    val uiState by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refreshMoodAndMessage()
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "Noxy Couple", style = MaterialTheme.typography.headlineMedium)

            MoodSection(
                title = "Ton humeur",
                mood = uiState.myMood,
                onMoodSelected = viewModel::setMyMood
            )

            uiState.partnerMood?.let { partnerMood ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(partnerMood.moodColor()))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Humeur de ${'$'}{DEMO_PARTNER_NAME}", fontWeight = FontWeight.Bold)
                        Text(text = "${'$'}{partnerMood.emoji()} ${'$'}{partnerMood.label()}")
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Dernier message", fontWeight = FontWeight.Bold)
                    val last = uiState.lastMessage
                    if (last != null) {
                        Text(text = "De : ${'$'}{last.fromUser}")
                        Text(text = last.text)
                        Text(text = "à ${'$'}{last.timestamp}", style = MaterialTheme.typography.labelSmall)
                    } else {
                        Text(text = "Pas encore de message 💌")
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = uiState.inputText,
                    onValueChange = viewModel::onInputChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Ton message") },
                    enabled = !uiState.isSending
                )
                Button(
                    onClick = viewModel::sendCoupleMessage,
                    enabled = !uiState.isSending
                ) {
                    Text(text = "Envoyer 💌")
                }
            }

            uiState.errorMessage?.let { error ->
                Text(text = error, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun MoodSection(title: String, mood: NoxyMood, onMoodSelected: (NoxyMood) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "$title : ${'$'}{mood.emoji()} ${'$'}{mood.label()}", fontWeight = FontWeight.Bold)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            NoxyMood.values().toList().chunked(3).forEach { rowMoods ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    rowMoods.forEach { option ->
                        MoodChip(
                            selected = option == mood,
                            mood = option,
                            onClick = { onMoodSelected(option) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MoodChip(selected: Boolean, mood: NoxyMood, onClick: () -> Unit) {
    val background = if (selected) Color(mood.moodColor()) else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (selected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(background)
            .clickable(indication = null, interactionSource = interactionSource, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .fillMaxWidth(0.45f),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = mood.emoji())
        Text(text = mood.label(), color = textColor)
    }
}

private const val DEMO_PARTNER_NAME = "Partenaire"
