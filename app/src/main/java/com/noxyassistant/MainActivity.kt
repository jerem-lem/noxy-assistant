package com.noxyassistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.noxyassistant.ui.couple.NoxyCoupleScreen
import com.noxyassistant.ui.couple.NoxyCoupleViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val coupleViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[NoxyCoupleViewModel::class.java]

        setContent {
            Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                when (intent?.action) {
                    ACTION_COUPLE_MESSAGE -> NoxyCoupleScreen(viewModel = coupleViewModel)
                    ACTION_GUARDIAN -> GuardianScreen()
                    ACTION_TALK, null -> ChatScreen()
                    else -> ChatScreen()
                }
            }
        }
    }

    companion object {
        const val ACTION_COUPLE_MESSAGE = "COUPLE_MESSAGE"
        const val ACTION_GUARDIAN = "GUARDIAN"
        const val ACTION_TALK = "TALK"
    }
}

@Composable
private fun ChatScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Écran de chat Noxy")
    }
}

@Composable
private fun GuardianScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Écran Guardian")
    }
}
