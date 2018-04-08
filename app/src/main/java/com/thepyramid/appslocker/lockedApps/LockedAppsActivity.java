package com.thepyramid.appslocker.lockedApps;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.MenuItem;
import android.view.Window;

import com.thepyramid.appslocker.R;
import com.thepyramid.appslocker.changePattern.ChangePatternActivity;
import com.thepyramid.appslocker.changePattern.ChangePatternFragment;
import com.thepyramid.appslocker.common.Base.BaseActivity;
import com.thepyramid.appslocker.common.Helpers.Utils;

public class LockedAppsActivity extends BaseActivity {

    private Toolbar toolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (Utils.isLollipop()) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locked_apps);
        initializeViews();
        setToolbar(toolBar, getString(R.string.yourLockedApps), true);
        if (savedInstanceState == null) {
            if (Utils.isLollipop()) {
                Transition enterTansition = TransitionInflater.from(this).inflateTransition(R.transition.slide);
                getWindow().setEnterTransition(enterTansition);
                getWindow().setAllowEnterTransitionOverlap(false);
            }
            loadFragment(LockedAppsFragment.newInstance());
        }
    }

    @Override
    protected void initializeViews() {
        toolBar = findViewById(R.id.toolBar);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frmFragmentContainer, fragment);
        fragmentTransaction.commit();
    }

    public static void openLockedAppsActivity(Context context) {
        Intent intent = new Intent(context, LockedAppsActivity.class);
        context.startActivity(intent);
    }


    public static void openLockedAppsActivity(Context context, Bundle bundle) {
        Intent intent = new Intent(context, LockedAppsActivity.class);
        context.startActivity(intent, bundle);
    }
}
