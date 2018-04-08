package com.thepyramid.appslocker.lockedApps;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

import static com.thepyramid.appslocker.common.LockedAppsContentProvider.APPS_LOCKED_CONTENT_URI;

/**
 * Created by samar ezz on 4/7/2018.
 */

public class LockedAppsLoader extends CursorLoader {

    public LockedAppsLoader(Context context) {
        super(context);
    }

    public static LockedAppsLoader getAllLockedApps(Context context) {
        return new LockedAppsLoader(context, APPS_LOCKED_CONTENT_URI);
    }

    public LockedAppsLoader(Context context, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        super(context, uri, projection, selection, selectionArgs, sortOrder);
    }

    private LockedAppsLoader(Context context, Uri uri) {
        super(context, uri, null, null, null, null);
    }
}
