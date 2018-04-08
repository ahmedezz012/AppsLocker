package com.thepyramid.appslocker.changePattern;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.MenuItem;
import android.view.Window;

import com.thepyramid.appslocker.common.Base.BaseActivity;
import com.thepyramid.appslocker.R;
import com.thepyramid.appslocker.common.Helpers.Utils;

public class ChangePatternActivity extends BaseActivity {

    private Toolbar toolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Utils.isLollipop()) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pattern);
        initializeViews();
        setToolbar(toolBar, getString(R.string.changePattern), true);


        if (savedInstanceState == null) {
            if (Utils.isLollipop()) {
                Transition enterTansition = TransitionInflater.from(this).inflateTransition(R.transition.slide);
                getWindow().setEnterTransition(enterTansition);
                getWindow().setAllowEnterTransitionOverlap(false);
            }
            loadFragment(ChangePatternFragment.newInstance());
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

    public static void openChangePatternActivity(Context context) {
        Intent intent = new Intent(context, ChangePatternActivity.class);
        context.startActivity(intent);
    }

    public static void openChangePatternActivity(Context context, Bundle bundle) {
        Intent intent = new Intent(context, ChangePatternActivity.class);
        context.startActivity(intent, bundle);
    }
}
