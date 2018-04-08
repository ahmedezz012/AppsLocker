package com.thepyramid.appslocker.apps;

import android.app.ActivityOptions;
import android.appwidget.AppWidgetManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.thepyramid.appslocker.common.Base.BaseActivity;
import com.thepyramid.appslocker.common.Helpers.Utils;
import com.thepyramid.appslocker.common.LockedAppsContentProvider;
import com.thepyramid.appslocker.R;
import com.thepyramid.appslocker.changePattern.ChangePatternActivity;
import com.thepyramid.appslocker.lockedApps.LockedAppsActivity;
import com.thepyramid.appslocker.widget.LockedAppsWidget;

import java.io.ByteArrayOutputStream;

import static com.thepyramid.appslocker.changePattern.ChangePatternActivity.openChangePatternActivity;
import static com.thepyramid.appslocker.common.Helpers.SqliteHelper.*;
import static com.thepyramid.appslocker.common.LockedAppsContentProvider.APPS_LOCKED_CONTENT_URI;
import static com.thepyramid.appslocker.lockedApps.LockedAppsActivity.openLockedAppsActivity;

public class ActivityApps extends BaseActivity implements AppsAdapter.BtnAppLockerListner {

    private Toolbar toolBar;
    private FirebaseAnalytics mFirebaseAnalytics;
    private static final String EVENT_NAME_LOCKED = "LOCKED";
    private static final String EVENT_NAME_UN_LOCKED = "UN_LOCKED";
    private static final String APP_NAME = "APP_NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Utils.isLollipop()) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        initializeViews();
        setToolbar(toolBar, getString(R.string.app_name), false);
        Utils.requestUsageStatsPermission(this);
        if (savedInstanceState == null) {
            if (Utils.isLollipop()) {
                Transition enterTansition = TransitionInflater.from(this).inflateTransition(R.transition.slide);
                getWindow().setEnterTransition(enterTansition);
                getWindow().setAllowEnterTransitionOverlap(false);
            }
            loadFragment(FragmentApps.newInstance());
        }
    }

    private void logLockedOrOpenedAppApp(String eventName, String appName) {
        Bundle bundle = new Bundle();
        bundle.putString(APP_NAME, appName);
        mFirebaseAnalytics.logEvent(eventName, bundle);
    }


    @Override
    protected void initializeViews() {
        toolBar = findViewById(R.id.toolBar);
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frmFragmentContainer, fragment);
        fragmentTransaction.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.more_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuShowLockedApps:
                if (Utils.isLollipop()) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this);
                    LockedAppsActivity.openLockedAppsActivity(this, options.toBundle());
                } else {
                    LockedAppsActivity.openLockedAppsActivity(this);
                }
                break;
            case R.id.menuChangePattern:
                if (Utils.isLollipop()) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this);
                    ChangePatternActivity.openChangePatternActivity(this, options.toBundle());
                } else {
                    ChangePatternActivity.openChangePatternActivity(this);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public static void openActivityApps(Context context) {
        Intent intent = new Intent(context, ActivityApps.class);
        context.startActivity(intent);
        ((AppCompatActivity) context).finish();
    }

    public static void openActivityApps(Context context, Bundle bundle) {
        Intent intent = new Intent(context, ActivityApps.class);
        context.startActivity(intent, bundle);
        ((AppCompatActivity) context).finish();
    }

    @Override
    public void lock(ApplicationInfo applicationInfo) {

        logLockedOrOpenedAppApp(EVENT_NAME_LOCKED, applicationInfo.loadLabel(getPackageManager()).toString());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        addAppToDb(applicationInfo);
        updateWidget();
    }

    @Override
    public void open(ApplicationInfo applicationInfo) {
        logLockedOrOpenedAppApp(EVENT_NAME_UN_LOCKED, applicationInfo.loadLabel(getPackageManager()).toString());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        removeAppFromDb(applicationInfo);
        updateWidget();
    }


    private Uri addAppToDb(ApplicationInfo applicationInfo) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(PACKAGE_NAME_COLUMN, applicationInfo.packageName);
        contentValues.put(APP_NAME_COLUMN, applicationInfo.loadLabel(getPackageManager()).toString());
        contentValues.put(IMAGE_COLUMN, castDrawableToByte(applicationInfo.loadIcon(getPackageManager())));
        return getContentResolver().insert(APPS_LOCKED_CONTENT_URI, contentValues);
    }

    private int removeAppFromDb(ApplicationInfo applicationInfo) {

        return getContentResolver().delete(LockedAppsContentProvider.getAppsLockedByPackageName(applicationInfo.packageName), null, null);
    }

    private byte[] castDrawableToByte(Drawable drawable) {
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    private void updateWidget() {
        Intent intentUpdate = new Intent(this, LockedAppsWidget.class);
        intentUpdate.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        sendBroadcast(intentUpdate);
    }
}
