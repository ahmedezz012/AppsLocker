package com.thepyramid.appslocker.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.thepyramid.appslocker.R;

/**
 * Implementation of App Widget functionality.
 */
public class LockedAppsWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        Intent intent = new Intent(context, LockedAppsService.class);
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.locked_apps_widget);
        rv.setRemoteAdapter(R.id.lvLockedApps, intent);
        rv.setEmptyView(R.id.lvLockedApps, R.id.txtNoLockedApps);
        appWidgetManager.updateAppWidget(appWidgetId, rv);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
    }

    @Override
    public void onDisabled(Context context) {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        if (intent != null && intent.getAction() != null) {
            if (intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
                int appWidgetIds[] = appWidgetManager.getAppWidgetIds(new ComponentName(context, LockedAppsWidget.class));
                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.lvLockedApps);
            }
        }
    }
}

