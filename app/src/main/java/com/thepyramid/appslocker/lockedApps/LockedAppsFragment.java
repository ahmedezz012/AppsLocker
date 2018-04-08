package com.thepyramid.appslocker.lockedApps;


import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.thepyramid.appslocker.R;
import com.thepyramid.appslocker.common.Base.BaseFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class LockedAppsFragment extends BaseFragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private RecyclerView rvLockedApps;
    private Context context;
    private ProgressBar progress;
    private TextView txtNoLockedApps;
    private int scrollPosition = -1;
    private static final String SCROLL_POSITION = "SCROLL_POSITION";

    public LockedAppsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_locked_apps, container, false);
        context = getActivity();
        initializeViews(v);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
        if (savedInstanceState != null)
            scrollPosition = savedInstanceState.getInt(SCROLL_POSITION);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SCROLL_POSITION,
                ((LinearLayoutManager) rvLockedApps.getLayoutManager()).findFirstVisibleItemPosition());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getLoaderManager().destroyLoader(0);
    }

    @Override
    protected void initializeViews(View v) {
        rvLockedApps = v.findViewById(R.id.rvLockedApps);
        progress = v.findViewById(R.id.progress);
        txtNoLockedApps = v.findViewById(R.id.txtNoLockedApps);
    }

    @Override
    protected void setListeners() {

    }

    public static LockedAppsFragment newInstance() {
        return new LockedAppsFragment();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return LockedAppsLoader.getAllLockedApps(context);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        progress.setVisibility(View.GONE);
        if (data != null) {
            if (data.getCount() > 0) {
                LockedAppsAdapter lockedAppsAdapter = new LockedAppsAdapter(data, context);
                rvLockedApps.setAdapter(lockedAppsAdapter);
                rvLockedApps.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
                if (scrollPosition != -1)
                    rvLockedApps.scrollToPosition(scrollPosition);
            } else {
                txtNoLockedApps.setVisibility(View.VISIBLE);
            }
        } else {
            txtNoLockedApps.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
