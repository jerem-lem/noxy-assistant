package com.noxy.assistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.noxy.assistant.ui.guardian.NoxyGuardianScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val action = intent.getStringExtra("ACTION")

        setContent {
            NoxyAssistantApp(action = action)
        }
    }
}

@Composable
fun NoxyAssistantApp(action: String?) {
    Surface(color = MaterialTheme.colorScheme.background) {
        when (action) {
            "GUARDIAN" -> NoxyGuardianScreen()
            "COUPLE_MESSAGE" -> CouplePlaceholderScreen()
            else -> NoxyChatScreen()
        }
    }
}

@Composable
fun NoxyChatScreen(modifier: Modifier = Modifier) {
    Text(text = "Écran de chat Noxy", modifier = modifier)
}

@Composable
fun CouplePlaceholderScreen(modifier: Modifier = Modifier) {
    Text(text = "Mode Couple à venir", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun NoxyAssistantPreview() {
    NoxyAssistantApp(action = null)
}
