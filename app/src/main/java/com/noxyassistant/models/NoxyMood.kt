package com.noxyassistant.models

import android.graphics.Color
import androidx.annotation.DrawableRes

/**
 * Represents a Noxy mood state with helpers for UI display.
 */
enum class NoxyMood {
    HAPPY,
    CALM,
    TIRED,
    STRESSED,
    ANNOYED,
    IN_LOVE;

    fun label(): String = when (this) {
        HAPPY -> "Heureux"
        CALM -> "Calme"
        TIRED -> "Fatigué"
        STRESSED -> "Stressé"
        ANNOYED -> "Agacé"
        IN_LOVE -> "Amoureux"
    }

    fun emoji(): String = when (this) {
        HAPPY -> "😄"
        CALM -> "😌"
        TIRED -> "😴"
        STRESSED -> "😰"
        ANNOYED -> "😠"
        IN_LOVE -> "😍"
    }

    /**
     * Placeholder colors for each mood.
     */
    fun moodColor(): Int = when (this) {
        HAPPY -> Color.parseColor("#FFF59D")
        CALM -> Color.parseColor("#B2DFDB")
        TIRED -> Color.parseColor("#E0E0E0")
        STRESSED -> Color.parseColor("#EF9A9A")
        ANNOYED -> Color.parseColor("#FFCCBC")
        IN_LOVE -> Color.parseColor("#F8BBD0")
    }

    /**
     * Placeholder icon per mood using built-in Android drawables.
     */
    @DrawableRes
    fun moodIconRes(): Int = when (this) {
        HAPPY -> android.R.drawable.btn_star_big_on
        CALM -> android.R.drawable.ic_menu_week
        TIRED -> android.R.drawable.ic_menu_recent_history
        STRESSED -> android.R.drawable.ic_delete
        ANNOYED -> android.R.drawable.ic_menu_close_clear_cancel
        IN_LOVE -> android.R.drawable.btn_star_big_off
    }
}
