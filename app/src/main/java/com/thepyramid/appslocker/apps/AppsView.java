package com.thepyramid.appslocker.apps;

import android.content.pm.ApplicationInfo;

import java.util.ArrayList;

/**
 * Created by samar ezz on 8/12/2017.
 */

public interface AppsView  {

    void setAppsData(ArrayList<ApplicationInfo> applicationInfoArrayList);
    void showHideProgress(boolean show);
}
