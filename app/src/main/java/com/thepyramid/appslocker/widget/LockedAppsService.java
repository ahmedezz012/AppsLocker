package com.thepyramid.appslocker.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by samar ezz on 4/5/2018.
 */

public class LockedAppsService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new LockedAppsListFactory(this.getApplicationContext());
    }
}
