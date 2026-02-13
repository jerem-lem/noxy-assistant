package ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ui.components.NoxyAvatar
import ui.model.NoxyMood
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun NoxyChatScreen(
    currentMood: NoxyMood,
    isNoxyThinking: Boolean,
    lastMessageFromNoxy: Boolean,
    modifier: Modifier = Modifier
) {
    val isSpeaking = lastMessageFromNoxy && isNoxyThinking

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        NoxyAvatar(
            mood = currentMood,
            modifier = Modifier.height(180.dp),
            isSpeaking = isSpeaking
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Chat avec Noxy",
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = if (isSpeaking) "Noxy est en train de répondre..." else "Conversation détendue.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview
@Composable
fun NoxyChatScreenPreview() {
    var mood by remember { mutableStateOf(NoxyMood.HAPPY) }
    Surface {
        NoxyChatScreen(
            currentMood = mood,
            isNoxyThinking = true,
            lastMessageFromNoxy = true
        )
    }
}
