package com.noxy.assistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val actionLabel = when (intent?.action) {
            ACTION_GUARDIAN -> "Guardian"
            ACTION_COUPLE_MESSAGE -> "Couple"
            else -> "Talk"
        }
        setContent {
            MaterialTheme {
                Surface {
                    Text(text = "Ouverture: $actionLabel")
                }
            }
        }
    }

    companion object {
        const val ACTION_TALK = "TALK"
        const val ACTION_GUARDIAN = "GUARDIAN"
        const val ACTION_COUPLE_MESSAGE = "COUPLE_MESSAGE"
    }
}
