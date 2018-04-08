package com.thepyramid.appslocker.common.Base;

import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Created by samar ezz on 8/11/2017.
 */

public abstract class BaseFragment extends Fragment{
    protected abstract void initializeViews(View v);
    protected abstract void setListeners();
}
