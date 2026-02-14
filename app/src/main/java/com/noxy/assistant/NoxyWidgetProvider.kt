package com.noxy.assistant

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

class NoxyWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        appWidgetIds.forEach { appWidgetId ->
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        lastMessage: String? = null,
        currentMood: NoxyMood? = null
    ) {
        val mood = currentMood ?: NoxyMood.HAPPY
        val views = RemoteViews(context.packageName, R.layout.widget_noxy)

        views.setTextViewText(R.id.widget_title, "Noxy")
        val subtitle = "Humeur : ${mood.toString()}"
        views.setTextViewText(R.id.widget_subtext, subtitle)
        val message = lastMessage ?: "Noxy est prêt à t’aider 🚀"
        views.setTextViewText(R.id.widget_last_message, message)

        views.setInt(R.id.widget_root, "setBackgroundColor", mood.moodColor(context))
        views.setImageViewResource(R.id.noxy_avatar, mood.moodIconRes())

        val talkIntent = context.intentForAction(MainActivity.ACTION_TALK)
        val guardianIntent = context.intentForAction(MainActivity.ACTION_GUARDIAN)
        val coupleIntent = context.intentForAction(MainActivity.ACTION_COUPLE_MESSAGE)

        views.setOnClickPendingIntent(R.id.widget_root, talkIntent)
        views.setOnClickPendingIntent(R.id.widget_button_talk, talkIntent)
        views.setOnClickPendingIntent(R.id.widget_button_guardian, guardianIntent)
        views.setOnClickPendingIntent(R.id.widget_button_couple, coupleIntent)

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun Context.intentForAction(action: String): PendingIntent {
        val intent = Intent(this, MainActivity::class.java).apply {
            this.action = action
        }
        return PendingIntent.getActivity(
            this,
            action.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    companion object {
        fun updateAllWidgets(
            context: Context,
            lastMessage: String? = null,
            currentMood: NoxyMood? = null
        ) {
            val manager = AppWidgetManager.getInstance(context)
            val component = ComponentName(context, NoxyWidgetProvider::class.java)
            val ids = manager.getAppWidgetIds(component)
            ids.forEach { appWidgetId ->
                NoxyWidgetProvider().updateAppWidget(context, manager, appWidgetId, lastMessage, currentMood)
            }
        }
    }
}
