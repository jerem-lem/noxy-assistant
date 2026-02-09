package com.noxyassistant.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.widget.RemoteViews
import com.noxyassistant.models.NoxyMood

class NoxyWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        updateAllWidgets(context, appWidgetManager, appWidgetIds, null, null)
    }

    companion object {
        fun updateAllWidgets(
            context: Context,
            lastMessage: String?,
            currentMood: NoxyMood?,
            appWidgetManager: AppWidgetManager = AppWidgetManager.getInstance(context),
            appWidgetIds: IntArray = appWidgetManager.getAppWidgetIds(ComponentName(context, NoxyWidgetProvider::class.java))
        ) {
            val safeMood = currentMood ?: NoxyMood.HAPPY
            val messageText = lastMessage ?: "Pas de message couple"
            val moodText = "${'$'}{safeMood.emoji()} ${'$'}{safeMood.label()}"

            appWidgetIds.forEach { widgetId ->
                val views = RemoteViews(context.packageName, android.R.layout.simple_list_item_2)
                views.setTextViewText(android.R.id.text1, moodText)
                views.setTextViewText(android.R.id.text2, messageText)
                appWidgetManager.updateAppWidget(widgetId, views)
            }
        }
    }
}
