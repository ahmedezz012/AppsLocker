package com.thepyramid.appslocker.common.Helpers;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.thepyramid.appslocker.R;

import static com.rvalerio.fgchecker.Utils.hasUsageStatsPermission;

/**
 * Created by samar ezz on 4/6/2018.
 */

public class Utils {
    public static final String USER_PATTERN = "user_pattern";

    public static void requestUsageStatsPermission(final Context context) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && !hasUsageStatsPermission(context)) {
            showAlertDialog(context, context.getString(R.string.usageStatePermissionMessage), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    ((AppCompatActivity) context).startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS
                    ));
                    dialog.dismiss();
                }
            }, true);
        } else {
            requestOverlayPermission(context);
        }
    }

    public static void requestOverlayPermission(final Context context) {
        if (checkForSystemAlertWindowPermission(context)) {
            showAlertDialog(context, context.getString(R.string.overlayPermissionMessage), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    ((AppCompatActivity) context).startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + context.getPackageName())));
                    dialog.dismiss();
                }
            }, false);
        }
    }

    public static boolean checkForSystemAlertWindowPermission(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(context) == false) {
                return true;
            }
        }
        return false;
    }

    private static void showAlertDialog(final Context context, String message, DialogInterface.OnClickListener okClickListener, boolean addDismissListener) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(R.string.alert);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.ok),
                okClickListener);
        if (addDismissListener) {
            alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    requestOverlayPermission(context);
                }
            });
        }
        alertDialog.show();
    }

    public static boolean isLollipop()
    {
        return android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }



}
