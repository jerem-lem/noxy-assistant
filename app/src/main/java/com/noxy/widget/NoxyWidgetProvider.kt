package com.noxy.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.widget.RemoteViews

class NoxyWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        updateAllWidgets(context, appWidgetManager = appWidgetManager, appWidgetIds = appWidgetIds)
    }

    companion object {
        fun updateAllWidgets(
            context: Context,
            appWidgetManager: AppWidgetManager = AppWidgetManager.getInstance(context),
            appWidgetIds: IntArray? = null,
            lastMessage: String? = null,
            currentMood: NoxyMood? = null
        ) {
            val widgetIds = appWidgetIds ?: appWidgetManager.getAppWidgetIds(ComponentName(context, NoxyWidgetProvider::class.java))
            widgetIds.forEach { widgetId ->
                updateAppWidget(context, appWidgetManager, widgetId, lastMessage, currentMood)
            }
        }

        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
            lastMessage: String? = null,
            currentMood: NoxyMood? = null
        ) {
            val mood = currentMood ?: NoxyMood.default
            val views = RemoteViews(context.packageName, R.layout.widget_noxy)

            views.setInt(R.id.widget_container, "setBackgroundColor", mood.moodColor(context))
            views.setTextViewText(R.id.widget_mood_tag, mood.emoji())

            val subtext = "Humeur : ${mood.emoji()} ${mood.label()} | Partenaire : —"
            views.setTextViewText(R.id.widget_subtext, subtext)

            val trimmedMessage = lastMessage?.takeIf { it.isNotBlank() }
                ?.let { message ->
                    val prefix = "De ton couple : "
                    val body = if (message.length > 40) message.take(40).trimEnd() + "…" else message
                    prefix + body
                }
                ?: context.getString(R.string.widget_default_message)

            views.setTextViewText(R.id.widget_last_message, trimmedMessage)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
