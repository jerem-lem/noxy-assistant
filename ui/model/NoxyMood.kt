package ui.model

import androidx.compose.ui.graphics.Color

enum class NoxyMood {
    HAPPY,
    CALM,
    TIRED,
    STRESSED,
    ANNOYED,
    IN_LOVE
}

data class NoxyAvatarColors(
    val baseColor: Color,
    val neonColor: Color,
    val eyeColor: Color
)

fun NoxyMood.toColors(): NoxyAvatarColors = when (this) {
    NoxyMood.HAPPY -> NoxyAvatarColors(
        baseColor = Color(0xFF40C4FF),
        neonColor = Color(0xFF18FFFF),
        eyeColor = Color(0xFFB3E5FC)
    )
    NoxyMood.CALM -> NoxyAvatarColors(
        baseColor = Color(0xFF3F51B5),
        neonColor = Color(0xFF7C4DFF),
        eyeColor = Color(0xFFBBDEFB)
    )
    NoxyMood.TIRED -> NoxyAvatarColors(
        baseColor = Color(0xFF607D8B),
        neonColor = Color(0xFFB0BEC5),
        eyeColor = Color(0xFFE0E0E0)
    )
    NoxyMood.STRESSED -> NoxyAvatarColors(
        baseColor = Color(0xFFFF5252),
        neonColor = Color(0xFFFF4081),
        eyeColor = Color(0xFFFFCDD2)
    )
    NoxyMood.ANNOYED -> NoxyAvatarColors(
        baseColor = Color(0xFFFFA000),
        neonColor = Color(0xFFFFC107),
        eyeColor = Color(0xFFFFF59D)
    )
    NoxyMood.IN_LOVE -> NoxyAvatarColors(
        baseColor = Color(0xFFEC407A),
        neonColor = Color(0xFFBA68C8),
        eyeColor = Color(0xFFF8BBD0)
    )
}
