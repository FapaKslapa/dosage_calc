package com.example.dosagecalc.presentation.ui.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import kotlinx.coroutines.delay

object WidgetUpdateHelper {
    suspend fun updateAllWidgets(context: Context) {
        delay(300) 
        val manager = GlanceAppWidgetManager(context)
        
        manager.getGlanceIds(NextReminderWidget::class.java).forEach { id ->
            NextReminderWidget().update(context, id)
        }
        
        manager.getGlanceIds(LastDrugWidget::class.java).forEach { id ->
            LastDrugWidget().update(context, id)
        }
    }
}
