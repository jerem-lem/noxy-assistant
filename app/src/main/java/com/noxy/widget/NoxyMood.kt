package com.noxy.widget

import android.content.Context
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.noxy.widget.R

enum class NoxyMood {
    HAPPY,
    CALM,
    TIRED,
    SAD,
    ANGRY;

    fun emoji(): String = when (this) {
        HAPPY -> "😀"
        CALM -> "😊"
        TIRED -> "😴"
        SAD -> "😢"
        ANGRY -> "😠"
    }

    fun label(): String = when (this) {
        HAPPY -> "Content"
        CALM -> "Apaisé"
        TIRED -> "Fatigué"
        SAD -> "Triste"
        ANGRY -> "Vexé"
    }

    @ColorInt
    fun moodColor(context: Context): Int = when (this) {
        HAPPY -> ContextCompat.getColor(context, R.color.mood_happy)
        CALM -> ContextCompat.getColor(context, R.color.mood_calm)
        TIRED -> ContextCompat.getColor(context, R.color.mood_tired)
        SAD -> ContextCompat.getColor(context, R.color.mood_sad)
        ANGRY -> ContextCompat.getColor(context, R.color.mood_angry)
    }

    companion object {
        val default: NoxyMood = HAPPY
    }
}
