package com.george.mydeliciousoven;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class OvenWidgetProvider extends AppWidgetProvider {

    private static final String FIRST_TEXT_WIDGET = "Ingredients of desired app";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, String ingredients,
                                int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.oven_widget_provider);

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        views.setOnClickPendingIntent(R.id.appwidget_text, pendingIntent);

        if (!ingredients.equals("")) {
            views.setTextViewText(R.id.appwidget_text, ingredients);
        }else{
            views.setTextViewText(R.id.appwidget_text,FIRST_TEXT_WIDGET);
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
        Log.e("appwidgetId", String.valueOf(appWidgetId));
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        String string = "";
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, string, appWidgetId);
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

    public static void updateWidgetWithIngredents(RecipeDetails context, AppWidgetManager appWidgetManager, String ingredienti, int[] widgetId) {

        for (int appWidgetId : widgetId) {
            updateAppWidget(context, appWidgetManager, ingredienti, appWidgetId);
        }

    }
}

