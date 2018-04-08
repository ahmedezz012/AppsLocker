package com.thepyramid.appslocker.widget;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.thepyramid.appslocker.common.Helpers.ContentProviderHelper;
import com.thepyramid.appslocker.common.Models.LockedApp;
import com.thepyramid.appslocker.R;

import java.util.ArrayList;

import static com.thepyramid.appslocker.common.Helpers.SqliteHelper.APP_NAME_INDEX;
import static com.thepyramid.appslocker.common.Helpers.SqliteHelper.IMAGE_INDEX;

/**
 * Created by samar ezz on 4/5/2018.
 */


public class LockedAppsListFactory implements RemoteViewsService.RemoteViewsFactory {


    private Context context;
    private ArrayList<LockedApp> lockedAppArrayList;

    public LockedAppsListFactory(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        lockedAppArrayList = getLockedAppArrayList();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if (lockedAppArrayList != null && lockedAppArrayList.size() > 0)
            return lockedAppArrayList.size();
        return 0;
    }

    @Override
    public RemoteViews getViewAt(int i) {
        if (lockedAppArrayList != null && lockedAppArrayList.size() > 0) {
            LockedApp lockedApp = lockedAppArrayList.get(i);
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.item_locked_app);
            views.setImageViewBitmap(R.id.imgAppIcon, BitmapFactory.decodeByteArray(lockedApp.getImage(), 0,
                    lockedApp.getImage().length));
            views.setTextViewText(R.id.txtAppName, lockedApp.getName());
            return views;
        }
        return null;
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
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }


    private ArrayList<LockedApp> getLockedAppArrayList()
    {
        ArrayList<LockedApp> lockedAppArrayList = new ArrayList<>();
        Cursor cursor= ContentProviderHelper.getAllLockedAppsCursor(context);
        if (cursor!=null)
        {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                LockedApp lockedApp = new LockedApp( cursor.getString(APP_NAME_INDEX),cursor.getBlob(IMAGE_INDEX));
               lockedAppArrayList.add(lockedApp);
                cursor.moveToNext();
            }
        }
        return lockedAppArrayList;
    }

}

