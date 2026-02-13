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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ui.components.NoxyAvatar
import ui.model.NoxyMood

@Composable
fun NoxyProfileScreen(
    currentMood: NoxyMood?,
    modifier: Modifier = Modifier
) {
    val moodToUse = currentMood ?: NoxyMood.HAPPY
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        NoxyAvatar(
            mood = moodToUse,
            modifier = Modifier.height(200.dp),
            isSpeaking = false
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Profil de Noxy",
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = "Humeur actuelle : ${'$'}moodToUse",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview
@Composable
private fun NoxyProfileScreenPreview() {
    Surface {
        NoxyProfileScreen(currentMood = NoxyMood.STRESSED)
    }
}
