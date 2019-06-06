package xyz.flyinghealthtimer;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class MainWidget extends AppWidgetProvider {

    RemoteViews remoteViews;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        //CharSequence widgetText = "Hello";
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.main_widget);
        //views.setTextViewText(R.id.widget_layout, widgetText);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private PendingIntent buildURIIntent(Context context) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(new ComponentName(context, TimerListActivity.class));
        return PendingIntent.getActivity(context, 0, intent, 0);

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int i = 0; i < appWidgetIds.length; i ++) {
            Log.i("SSS" , "onUpdate");
            if (remoteViews != null)
                appWidgetManager.updateAppWidget(appWidgetIds[i], remoteViews);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        String action = intent.getAction();
        Bundle extras = intent.getExtras();

        int[] appwidgetIds = extras.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS);


        if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(action)) {

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.main_widget);
            views.setOnClickPendingIntent(R.id.textView, buildURIIntent(context));
            remoteViews = views;
            this.onUpdate(context, AppWidgetManager.getInstance(context),appwidgetIds);
        }

    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

