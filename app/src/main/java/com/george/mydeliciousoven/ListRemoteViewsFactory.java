package com.george.mydeliciousoven;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

public class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private static String ingredientsForList = "";
    private RemoteViews views;

    Context mContext;

    public ListRemoteViewsFactory(Context context) {
        mContext = context;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public RemoteViews getViewAt(int position) {

        views = new RemoteViews(mContext.getPackageName(), R.layout.oven_widget_provider);

        if (!ingredientsForList.equals("")) {
            views.setTextViewText(R.id.appwidget_text, ingredientsForList);
        } else {
            views.setTextViewText(R.id.appwidget_text, OvenWidgetProvider.FIRST_TEXT_WIDGET);
        }

        Intent fillInIntent = new Intent();
        fillInIntent.putExtra("row", position);
        views.setOnClickFillInIntent(R.id.appwidget_text, fillInIntent);

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    public static void setIngredientData(String string) {
        ingredientsForList = string;
    }
}
