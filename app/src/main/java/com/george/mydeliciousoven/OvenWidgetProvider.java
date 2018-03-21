package com.george.mydeliciousoven;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class OvenWidgetProvider extends AppWidgetProvider {

    public static final String FIRST_TEXT_WIDGET = "Ingredients of desired app";
    private static final String TEXT_FOR_LIST_VIEW = "text_for_listView";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, String ingredients, String recipeName,
                                int appWidgetId) {

        /*
        Use below code if you want to display ingredients in one Textview inside widget

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.oven_widget_provider);

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        views.setOnClickPendingIntent(R.id.appwidget_text, pendingIntent);

        if (!ingredients.equals("")) {
            views.setTextViewText(R.id.appwidget_text, ingredients);
        }else{
            views.setTextViewText(R.id.appwidget_text,FIRST_TEXT_WIDGET);
        }*/

        RemoteViews views = getListViewRemoteViews(context, ingredients, recipeName);
        //trigger onDatasetChende in ViewsFactory
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_list_view);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private static RemoteViews getListViewRemoteViews(Context context, String ingredients, String recipeName) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_list_view);
        // Set the GridWidgetService intent to act as the adapter for the ListView
        Intent intent = new Intent(context, ListWidgetService.class);
        intent.putExtra(TEXT_FOR_LIST_VIEW, ingredients);
        views.setRemoteAdapter(R.id.widget_list_view, intent);
        //set text to header
        views.setTextViewText(R.id.headerForWidget, context.getString(R.string.ingredients_for) + " " + recipeName);
        views.setViewVisibility(R.id.headerForWidget, View.VISIBLE);

        //Setting click listener to open MainActivity
        Intent appIntent = new Intent(context, MainActivity.class);
        PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.widget_list_view, appPendingIntent);

        ListRemoteViewsFactory.setIngredientData(ingredients);
        views.setEmptyView(R.id.widget_list_view, R.id.empty_view);
        return views;
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        String string = "";
        String string2 = "";
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, string, string2, appWidgetId);
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

    public static void updateWidgetWithIngredents(RecipeDetails context, AppWidgetManager appWidgetManager, String ingredienti, String recipeName, int[] widgetId) {

        for (int appWidgetId : widgetId) {
            updateAppWidget(context, appWidgetManager, ingredienti, recipeName, appWidgetId);
        }

    }
}
