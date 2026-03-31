package com.fleet.ledger.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.fleet.ledger.R

/**
 * Ana ekran widget'ı
 * Günlük özet bilgileri gösterir
 */
class FleetSummaryWidget : AppWidgetProvider() {
    
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }
    
    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        // TODO: Veritabanından güncel verileri çek
        val views = RemoteViews(context.packageName, R.layout.widget_fleet_summary)
        
        // Widget verilerini güncelle
        views.setTextViewText(R.id.widget_daily_income, "₺0")
        views.setTextViewText(R.id.widget_daily_expense, "₺0")
        views.setTextViewText(R.id.widget_daily_profit, "₺0")
        views.setTextViewText(R.id.widget_active_vehicles, "0")
        
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}
