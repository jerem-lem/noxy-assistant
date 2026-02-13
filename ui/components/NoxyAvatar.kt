package ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ui.model.NoxyAvatarColors
import ui.model.NoxyMood
import ui.model.toColors

@Composable
fun NoxyAvatar(
    mood: NoxyMood,
    modifier: Modifier = Modifier,
    isSpeaking: Boolean = false
) {
    val colors = mood.toColors()

    val pulseDuration = if (isSpeaking) 900 else 1500
    val blinkDuration = if (isSpeaking) 1800 else 2400

    val infiniteTransition = rememberInfiniteTransition(label = "noxy-avatar-transition")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = pulseDuration, easing = { it * it }),
            repeatMode = RepeatMode.Reverse
        ),
        label = "halo-alpha"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isSpeaking) 1.15f else 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = pulseDuration, easing = { it * it }),
            repeatMode = RepeatMode.Reverse
        ),
        label = "halo-scale"
    )

    val blinkScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = blinkDuration
                1f at 0
                1f at blinkDuration - 260
                0.15f at blinkDuration - 180
                1f at blinkDuration
            }
        ),
        label = "blink-scale"
    )

    Box(modifier = modifier.aspectRatio(1f)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawHalo(colors, pulseAlpha, pulseScale)
            drawHead(colors)
            drawEyes(colors, blinkScale)
            drawTorso(colors)
        }
    }
}

private fun DrawScope.drawHalo(colors: NoxyAvatarColors, alpha: Float, scale: Float) {
    val radius = size.minDimension / 2f * 0.9f * scale
    drawCircle(
        color = colors.neonColor.copy(alpha = alpha * 0.8f),
        radius = radius,
        center = center,
        style = Stroke(width = size.minDimension * 0.025f)
    )
}

private fun DrawScope.drawHead(colors: NoxyAvatarColors) {
    val headRadius = size.minDimension / 2.8f
    drawCircle(
        color = colors.baseColor.copy(alpha = 0.9f),
        radius = headRadius,
        center = center
    )

    // Inner glow
    drawCircle(
        color = colors.neonColor.copy(alpha = 0.35f),
        radius = headRadius * 0.9f,
        center = center,
        style = Stroke(width = size.minDimension * 0.02f)
    )
}

private fun DrawScope.drawEyes(colors: NoxyAvatarColors, blinkScale: Float) {
    val eyeRadius = size.minDimension * 0.06f
    val eyeOffsetX = size.minDimension * 0.14f
    val eyeCenterY = center.y - size.minDimension * 0.06f
    val verticalScale = blinkScale.coerceIn(0.1f, 1f)

    fun DrawScope.drawEye(center: Offset) {
        withTransform({
            scale(1f, verticalScale, pivot = center)
        }) {
            drawCircle(
                color = colors.eyeColor,
                radius = eyeRadius,
                center = center
            )
            drawCircle(
                color = colors.neonColor.copy(alpha = 0.6f),
                radius = eyeRadius * 0.5f,
                center = center + Offset(eyeRadius * 0.2f, -eyeRadius * 0.2f)
            )
        }
    }

    drawEye(center.copy(x = center.x - eyeOffsetX, y = eyeCenterY))
    drawEye(center.copy(x = center.x + eyeOffsetX, y = eyeCenterY))
}

private fun DrawScope.drawTorso(colors: NoxyAvatarColors) {
    val torsoWidth = size.minDimension * 0.3f
    val torsoHeight = size.minDimension * 0.18f
    val topLeft = Offset(center.x - torsoWidth / 2, center.y + size.minDimension * 0.2f)
    val torsoRect = Size(torsoWidth, torsoHeight)

    drawRoundRect(
        color = colors.baseColor.copy(alpha = 0.85f),
        topLeft = topLeft,
        size = torsoRect,
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(torsoHeight / 2, torsoHeight / 2)
    )

    val hexagonRadius = torsoHeight * 0.55f
    val hexPath = Path().apply {
        val centerHex = Offset(center.x, topLeft.y + torsoHeight / 2)
        for (i in 0..5) {
            val angle = Math.toRadians((60 * i - 30).toDouble())
            val x = centerHex.x + hexagonRadius * kotlin.math.cos(angle).toFloat()
            val y = centerHex.y + hexagonRadius * kotlin.math.sin(angle).toFloat()
            if (i == 0) moveTo(x, y) else lineTo(x, y)
        }
        close()
    }

    drawPath(
        path = hexPath,
        color = colors.neonColor.copy(alpha = 0.7f),
        style = Stroke(width = size.minDimension * 0.014f)
    )
}

@Preview
@Composable
private fun NoxyAvatarPreview() {
    NoxyAvatar(
        mood = NoxyMood.HAPPY,
        modifier = Modifier.size(200.dp)
    )
}
