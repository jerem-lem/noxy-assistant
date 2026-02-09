package ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ui.components.NoxyAvatar
import ui.model.NoxyMood

@Composable
fun NoxyCoupleScreen(
    myMood: NoxyMood,
    partnerMood: NoxyMood?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Ton Noxy", style = MaterialTheme.typography.labelLarge)
            NoxyAvatar(
                mood = myMood,
                modifier = Modifier.height(150.dp),
                isSpeaking = false
            )
        }

        if (partnerMood != null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Partenaire", style = MaterialTheme.typography.labelLarge)
                NoxyAvatar(
                    mood = partnerMood,
                    modifier = Modifier.height(130.dp),
                    isSpeaking = true
                )
            }
        }
    }
}

@Preview
@Composable
private fun NoxyCoupleScreenPreview() {
    Surface {
        NoxyCoupleScreen(
            myMood = NoxyMood.CALM,
            partnerMood = NoxyMood.IN_LOVE
        )
    }
}
