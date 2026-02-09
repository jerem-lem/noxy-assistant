package com.noxy.assistant

import android.content.Context
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat

/**
 * Simple representation of Noxy's mood so the widget can reflect it visually.
 */
enum class NoxyMood {
    HAPPY,
    CALM,
    FOCUSED,
    SERIOUS;

    @ColorInt
    fun moodColor(context: Context): Int {
        val colorRes = when (this) {
            HAPPY -> android.R.color.holo_orange_light
            CALM -> android.R.color.holo_blue_light
            FOCUSED -> android.R.color.holo_green_light
            SERIOUS -> android.R.color.darker_gray
        }
        return ContextCompat.getColor(context, colorRes)
    }

    @DrawableRes
    fun moodIconRes(): Int = when (this) {
        HAPPY -> R.drawable.ic_noxy_avatar
        CALM -> R.drawable.ic_noxy_avatar
        FOCUSED -> R.drawable.ic_noxy_avatar
        SERIOUS -> R.drawable.ic_noxy_avatar
    }

    override fun toString(): String {
        return name.lowercase().replaceFirstChar { it.uppercase() }
    }
}
