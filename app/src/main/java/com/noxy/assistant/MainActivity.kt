package com.noxy.assistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.noxy.assistant.ui.chat.NoxyChatScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val action = intent?.action
        setContent {
            NoxyAppRoot(action = action)
        }
    }
}

@Composable
fun NoxyAppRoot(action: String?) {
    Surface(color = Color(0xFF0D0D0D), modifier = Modifier) {
        when (action) {
            null, "TALK" -> NoxyChatScreen()
            else -> NoxyChatScreen()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NoxyPreview() {
    MaterialTheme {
        NoxyAppRoot(action = "TALK")
    }
}
