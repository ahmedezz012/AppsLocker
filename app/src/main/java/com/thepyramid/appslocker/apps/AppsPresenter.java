package com.thepyramid.appslocker.apps;


import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by samar ezz on 8/12/2017.
 */

public class AppsPresenter  {
    private AppsView appsView;
    private PackageManager packageManager;
    private Context context;

    public AppsPresenter(AppsView appsView,PackageManager packageManager,Context context) {
        this.appsView = appsView;
        this.packageManager = packageManager;
        this.context = context;
    }

    public void getAllApps()
    {
        MyTask myTask =new MyTask();
        myTask.execute();
    }


    private class MyTask extends AsyncTask<Void, Void, ArrayList<ApplicationInfo>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected ArrayList<ApplicationInfo> doInBackground(Void... voids) {
            List<ApplicationInfo> apps = packageManager.getInstalledApplications(0);
            ArrayList<ApplicationInfo> applicationInfoArrayList=checkForLaunchIntent(apps);
            return applicationInfoArrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<ApplicationInfo> applicationInfos) {
            super.onPostExecute(applicationInfos);
            appsView.showHideProgress(false);
            appsView.setAppsData(applicationInfos);
        }

        private ArrayList<ApplicationInfo> checkForLaunchIntent(List<ApplicationInfo> list) {
            ArrayList<ApplicationInfo> applist = new ArrayList<>();
            for (ApplicationInfo info : list) {
                try {
                    if (null != packageManager.getLaunchIntentForPackage(info.packageName) &&
                            !info.packageName.equals(context.getPackageName())) {
                        applist.add(info);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return applist;
        }
    }
}
