package com.thepyramid.appslocker.common.Base;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

/**
 * Created by samar ezz on 8/11/2017.
 */

public abstract class BaseActivity extends AppCompatActivity {
    private Toolbar myToolbar;

    protected void setToolbar(Toolbar toolbar, String title, boolean showBackButton) {
        myToolbar = toolbar;
        myToolbar.setTitle(title);

        setSupportActionBar(myToolbar);


        if (showBackButton) {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
    }
    protected abstract void initializeViews();

}
