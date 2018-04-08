package com.thepyramid.appslocker.lockedApps;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thepyramid.appslocker.R;

import static com.thepyramid.appslocker.common.Helpers.SqliteHelper.APP_NAME_INDEX;
import static com.thepyramid.appslocker.common.Helpers.SqliteHelper.IMAGE_INDEX;

/**
 * Created by samar ezz on 4/7/2018.
 */

public class LockedAppsAdapter extends RecyclerView.Adapter<LockedAppsAdapter.LockedAppsViewHolder> {

    private Cursor cursor;
    private Context context;

    public LockedAppsAdapter(Cursor cursor, Context context) {
        this.cursor = cursor;
        this.context = context;
    }

    @Override
    public LockedAppsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LockedAppsViewHolder(LayoutInflater.from(context).inflate(R.layout.item_locked_app, parent, false));
    }

    @Override
    public void onBindViewHolder(LockedAppsViewHolder holder, int position) {
        cursor.moveToPosition(position);
        holder.txtAppName.setText(cursor.getString(APP_NAME_INDEX));
        holder.imgAppIcon.setImageBitmap(BitmapFactory.decodeByteArray(cursor.getBlob(IMAGE_INDEX), 0,
                cursor.getBlob(IMAGE_INDEX).length));
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    public class LockedAppsViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgAppIcon;
        private TextView txtAppName;

        public LockedAppsViewHolder(View itemView) {
            super(itemView);
            imgAppIcon = itemView.findViewById(R.id.imgAppIcon);
            txtAppName = itemView.findViewById(R.id.txtAppName);
        }
    }
}
