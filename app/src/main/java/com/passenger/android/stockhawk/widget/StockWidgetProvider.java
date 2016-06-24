package com.passenger.android.stockhawk.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.passenger.android.stockhawk.R;
import com.passenger.android.stockhawk.ui.MyStocksActivity;

/**
 * Created by aman on 24/6/16.
 */
public class StockWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for(int appWidgetId : appWidgetIds){
            String widgetTitle = context.getString(R.string.app_name);
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_detail_view);
            remoteViews.setTextViewText(R.id.widget_header, widgetTitle);
            remoteViews.setRemoteAdapter(R.id.widget_list,
                    new Intent(context, StockRemoteViewsService.class));
            Intent intent = new Intent(context, MyStocksActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            remoteViews.setOnClickPendingIntent(R.id.widget_header, pendingIntent);
            remoteViews.setEmptyView(R.id.widget_list, R.id.empty_list_textview);
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if(intent.getAction() == MyStocksActivity.STOCK_UPDATED){
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context, getClass()));

            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list);
        }
    }
}
