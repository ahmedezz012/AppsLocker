package com.thepyramid.appslocker.common.Helpers;

import android.content.Context;
import android.database.Cursor;

import static com.thepyramid.appslocker.common.LockedAppsContentProvider.APPS_LOCKED_CONTENT_URI;

/**
 * Created by samar ezz on 4/5/2018.
 */

public class ContentProviderHelper {

    public static Cursor getAllLockedAppsCursor(Context context)
    {
        Cursor cursor = context.getContentResolver().query(APPS_LOCKED_CONTENT_URI, null, null, null, null);
        return cursor;
    }
}
