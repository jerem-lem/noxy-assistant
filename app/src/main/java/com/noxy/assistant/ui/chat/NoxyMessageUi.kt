package com.noxy.assistant.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun NoxyMessageBubble(message: NoxyChatViewModel.ChatMessage, modifier: Modifier = Modifier) {
    val isUser = message.isUser
    val bubbleColor = if (isUser) Color(0xFF1E88E5) else Color(0xFF424242)
    val textColor = Color.White

    Row(
        modifier = modifier,
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!isUser) {
            AvatarPlaceholder()
        }
        Surface(
            color = bubbleColor,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = message.text,
                color = textColor,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            )
        }
        if (isUser) {
            // Space for potential user avatar in the future
            Box(modifier = Modifier.size(24.dp))
        }
    }
}

@Composable
private fun AvatarPlaceholder() {
    Column(
        modifier = Modifier
            .padding(start = 8.dp, end = 4.dp, top = 4.dp)
            .size(32.dp)
            .clip(CircleShape)
            .background(Color(0xFF7E57C2)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "N",
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
