package com.thepyramid.appslocker.apps;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.thepyramid.appslocker.common.LockedAppsContentProvider;
import com.thepyramid.appslocker.R;

import java.util.List;

/**
 * Created by samar ezz on 8/12/2017.
 */

public class AppsAdapter extends RecyclerView.Adapter<AppsAdapter.AppsViewHolder> {


    private List<ApplicationInfo> applicationInfoArrayList;
    private Context context;
    private BtnAppLockerListner btnAppLockerListner;

    public AppsAdapter(Context context, List<ApplicationInfo> applicationInfoArrayList, BtnAppLockerListner btnAppLockerListner) {
        this.applicationInfoArrayList = applicationInfoArrayList;
        this.context = context;
        this.btnAppLockerListner = btnAppLockerListner;
    }

    @Override
    public AppsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_application, parent, false);
        return new AppsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AppsViewHolder holder, int position) {
        ApplicationInfo applicationInfo = applicationInfoArrayList.get(position);
        holder.imgAppIcon.setImageDrawable(applicationInfo.loadIcon(context.getPackageManager()));
        holder.txtAppName.setText(applicationInfo.loadLabel(context.getPackageManager()));
        Cursor cursor = context.getContentResolver().query(LockedAppsContentProvider.getAppsLockedByPackageName(applicationInfo.packageName),
                null, null, null, null);
        if (cursor != null){
            if (cursor.getCount()>0)
                holder.switchLockOrOpen.setChecked(true);
            else
                holder.switchLockOrOpen.setChecked(false);
        }else
            holder.switchLockOrOpen.setChecked(false);

    }

    public void setApplicationInfoArrayList(List<ApplicationInfo> applicationInfoArrayList) {
        this.applicationInfoArrayList = applicationInfoArrayList;
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return applicationInfoArrayList.size();
    }

    public class AppsViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgAppIcon;
        private TextView txtAppName;
        private Switch switchLockOrOpen;

        public AppsViewHolder(View itemView) {
            super(itemView);
            imgAppIcon = itemView.findViewById(R.id.imgAppIcon);
            txtAppName = itemView.findViewById(R.id.txtAppName);
            switchLockOrOpen = itemView.findViewById(R.id.switchLockOrOpen);
            switchLockOrOpen.setOnCheckedChangeListener(switchLockOrOpenCheckedChangeListener);
        }
        private CompoundButton.OnCheckedChangeListener switchLockOrOpenCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    btnAppLockerListner.lock(applicationInfoArrayList.get(getLayoutPosition()));
                else
                    btnAppLockerListner.open(applicationInfoArrayList.get(getLayoutPosition()));
            }
        };


    }

    public interface BtnAppLockerListner {
        void lock(ApplicationInfo applicationInfo);
        void open(ApplicationInfo applicationInfo);
    }
}
