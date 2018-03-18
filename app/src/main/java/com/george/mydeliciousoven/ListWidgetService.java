package com.george.mydeliciousoven;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by farmaker1 on 18/03/2018.
 */

public class ListWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(this.getApplicationContext());
    }
}

