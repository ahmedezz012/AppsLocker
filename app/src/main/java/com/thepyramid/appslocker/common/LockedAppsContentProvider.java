package com.thepyramid.appslocker.common;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import com.thepyramid.appslocker.common.Helpers.SqliteHelper;

public class LockedAppsContentProvider extends ContentProvider {
    public static final String AUTHORITY = "com.thepyramid.appslocker";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final Uri APPS_LOCKED_CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(SqliteHelper.LOCKED_APPS_TABLE_NAME).build();


    private static final int ALL_LOCKED_APPS = 100, LOCKED_APP_BY_PACKAGE_NAME = 101;

    private SqliteHelper sqliteHelper;
    private UriMatcher uriMatcher = buildURIMatcher();


    public static UriMatcher buildURIMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(AUTHORITY, SqliteHelper.LOCKED_APPS_TABLE_NAME, ALL_LOCKED_APPS);
        uriMatcher.addURI(AUTHORITY, SqliteHelper.LOCKED_APPS_TABLE_NAME + "/*", LOCKED_APP_BY_PACKAGE_NAME);

        return uriMatcher;
    }
    public LockedAppsContentProvider() {
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        sqliteHelper = new SqliteHelper(context);
        return true;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int match = uriMatcher.match(uri);
        String mSelection = "Packagename=?", packageName;
        String[] mSelectionArgs;
        packageName = uri.getPathSegments().get(1);
        mSelectionArgs = new String[]{packageName};
        int rows = 0;
        switch (match) {
            case LOCKED_APP_BY_PACKAGE_NAME:
                rows = sqliteHelper.deleteRow(SqliteHelper.LOCKED_APPS_TABLE_NAME, mSelection, mSelectionArgs);
                break;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rows;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri returnUri = null;
        int match = uriMatcher.match(uri);
        switch (match) {
            case ALL_LOCKED_APPS:
                long id = sqliteHelper.insertRow(SqliteHelper.LOCKED_APPS_TABLE_NAME, null, values);
                if (id > 0)
                    returnUri = ContentUris.withAppendedId(APPS_LOCKED_CONTENT_URI, id);
                break;
            default:
                throw new UnsupportedOperationException("UnKnow uri");
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        int match = uriMatcher.match(uri);
        String mSelection = "Packagename=?", packageName;
        String[] mSelectionArgs;
        Cursor cursor;
        switch (match) {
            case ALL_LOCKED_APPS:
                cursor = sqliteHelper.selectAll(SqliteHelper.LOCKED_APPS_TABLE_NAME);
                break;
            case LOCKED_APP_BY_PACKAGE_NAME:
                packageName = uri.getPathSegments().get(1);
                mSelectionArgs = new String[]{packageName};
                cursor = sqliteHelper.selectRow(SqliteHelper.LOCKED_APPS_TABLE_NAME, projection, mSelection, mSelectionArgs
                        , null, null, null);
                break;
            default:
                throw new UnsupportedOperationException("UnKnow uri");
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public static Uri getAppsLockedByPackageName(String packageName)
    {
        return APPS_LOCKED_CONTENT_URI.buildUpon().appendPath(packageName).build();
    }

}
