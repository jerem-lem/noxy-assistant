package com.noxy.assistant.models

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes

/**
 * Enumeration of the possible moods for Noxy.
 */
enum class NoxyMood {
    HAPPY,
    CALM,
    TIRED,
    STRESSED,
    ANNOYED,
    IN_LOVE
}

/**
 * Returns an ARGB color associated with this [NoxyMood].
 */
@ColorInt
fun NoxyMood.moodColor(): Int = when (this) {
    NoxyMood.HAPPY -> Color.parseColor("#FFEB3B") // Yellow
    NoxyMood.CALM -> Color.parseColor("#8BC34A")  // Green
    NoxyMood.TIRED -> Color.parseColor("#90A4AE") // Blue gray
    NoxyMood.STRESSED -> Color.parseColor("#F44336") // Red
    NoxyMood.ANNOYED -> Color.parseColor("#FF9800") // Orange
    NoxyMood.IN_LOVE -> Color.parseColor("#E91E63") // Pink
}

/**
 * Returns a placeholder drawable resource matching this [NoxyMood].
 */
@DrawableRes
fun NoxyMood.moodIconRes(): Int = when (this) {
    NoxyMood.HAPPY -> android.R.drawable.btn_star_big_on
    NoxyMood.CALM -> android.R.drawable.ic_menu_compass
    NoxyMood.TIRED -> android.R.drawable.ic_lock_idle_lock
    NoxyMood.STRESSED -> android.R.drawable.ic_delete
    NoxyMood.ANNOYED -> android.R.drawable.ic_dialog_alert
    NoxyMood.IN_LOVE -> android.R.drawable.btn_star_big_on
}
