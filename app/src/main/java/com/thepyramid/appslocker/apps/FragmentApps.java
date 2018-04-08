package com.thepyramid.appslocker.apps;


import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.thepyramid.appslocker.common.Base.BaseFragment;
import com.thepyramid.appslocker.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentApps extends BaseFragment implements AppsView {


    private static final String APPLICATION_INFO_ARRAY = "APPLICATION_INFO_ARRAY";
    private static final String APPS_LIST_SCROLL_POSITION = "APPS_LIST_SCROLL_POSITION";
    private RecyclerView rvApps;
    private Context context;
    private AppsAdapter appsAdapter;
    private PackageManager packageManager;
    private AppsPresenter appsPresenter;
    private ArrayList<ApplicationInfo> applicationInfoArrayList;
    private AppsAdapter.BtnAppLockerListner btnAppLockerListner;
    private AdView adView;
    private ProgressBar progress;

    public static FragmentApps newInstance() {
        FragmentApps fragment = new FragmentApps();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public FragmentApps() {

        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_apps, container, false);
        context = getActivity();
        initializeViews(view);
        return view;
    }

    @Override
    protected void initializeViews(View v) {
        rvApps = v.findViewById(R.id.rvApps);
        adView = v.findViewById(R.id.adView);
        progress = v.findViewById(R.id.progress);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        adView.loadAd(adRequest);
    }

    @Override
    protected void setListeners() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context != null) {
            try {
                btnAppLockerListner = (AppsAdapter.BtnAppLockerListner) context;
            } catch (Exception exc) {
                btnAppLockerListner = null;
            }
        } else {
            btnAppLockerListner = null;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        btnAppLockerListner = null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        packageManager = context.getPackageManager();
        appsPresenter = new AppsPresenter(this, packageManager, context);
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(APPLICATION_INFO_ARRAY)) {
                this.applicationInfoArrayList = savedInstanceState.getParcelableArrayList(APPLICATION_INFO_ARRAY);
                setAppsData(applicationInfoArrayList);
                showHideProgress(false);
                rvApps.scrollToPosition(savedInstanceState.getInt(APPS_LIST_SCROLL_POSITION));
            } else
                appsPresenter.getAllApps();
        } else
            appsPresenter.getAllApps();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (applicationInfoArrayList != null) {
            outState.putParcelableArrayList(APPLICATION_INFO_ARRAY, applicationInfoArrayList);
            outState.putInt(APPS_LIST_SCROLL_POSITION,
                    ((LinearLayoutManager) rvApps.getLayoutManager()).findFirstVisibleItemPosition());
        }
    }

    @Override
    public void setAppsData(ArrayList<ApplicationInfo> applicationInfoArrayList) {
        this.applicationInfoArrayList = applicationInfoArrayList;
        initAdapter();
    }

    @Override
    public void showHideProgress(boolean show) {
        if (show)
            progress.setVisibility(View.VISIBLE);
        else
            progress.setVisibility(View.GONE);
    }

    private void initAdapter() {
        appsAdapter = new AppsAdapter(context, applicationInfoArrayList, btnAppLockerListner);
        rvApps.setAdapter(appsAdapter);
        rvApps.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
    }
}
