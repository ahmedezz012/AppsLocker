package com.thepyramid.appslocker.common;

import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.rvalerio.fgchecker.AppChecker;
import com.thepyramid.appslocker.R;
import com.thepyramid.appslocker.common.Helpers.SharedPreferencesHelper;
import com.thepyramid.appslocker.common.Helpers.Utils;

import java.util.List;

import static com.thepyramid.appslocker.common.Helpers.SharedPreferencesHelper.DEFAULT_PASSWORD_KEY;
import static com.thepyramid.appslocker.common.Helpers.SqliteHelper.APP_NAME_INDEX;
import static com.thepyramid.appslocker.common.Helpers.SqliteHelper.IMAGE_INDEX;
import static com.thepyramid.appslocker.locker.LockerPresenter.CLEAR_PATTERN_DELAY;

public class ServiceChecking extends Service {
    private String previousApp;
    public static final int TIME_PERIOD = 2000;
    private Context context = null;
    private Dialog dialog;
    private Cursor mCursor;


    public ServiceChecking() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    AppChecker appChecker = new AppChecker();
                    String packageName = appChecker.getForegroundApp(getApplicationContext());
                    if (packageName != null && !packageName.equals(previousApp)) {
                        Cursor cursor = context.getContentResolver().query(LockedAppsContentProvider.getAppsLockedByPackageName(packageName),
                                null, null, null, null);
                        if (cursor != null) {
                            if (cursor.getCount() > 0) {
                                if (!Utils.checkForSystemAlertWindowPermission(context)) {
                                    mCursor = cursor;
                                    Handler h = new Handler(getMainLooper());
                                    h.post(showDialogRunnable);
                                }
                            }
                        }
                    }
                    previousApp = packageName;

                    try {
                        Thread.sleep(TIME_PERIOD);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        t.start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY_COMPATIBILITY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (dialog != null) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptsView = layoutInflater.inflate(R.layout.activity_locker, null);
        ImageView imgAppIcon = promptsView.findViewById(R.id.imgAppIcon);
        TextView txtUnlockAppName = promptsView.findViewById(R.id.txtUnlockAppName);
        mCursor.moveToFirst();
        txtUnlockAppName.setText(String.format(getString(R.string.unlock), mCursor.getString(APP_NAME_INDEX)));
        byte[] image = mCursor.getBlob(IMAGE_INDEX);

        imgAppIcon.setImageBitmap(BitmapFactory.decodeByteArray(image, 0,
                image.length));
        final PatternLockView patternLockView = promptsView.findViewById(R.id.plvHomePattern);

        patternLockView.addPatternLockListener(new PatternLockViewListener() {
            @Override
            public void onStarted() {

            }

            @Override
            public void onProgress(List<PatternLockView.Dot> progressPattern) {

            }

            @Override
            public void onComplete(List<PatternLockView.Dot> pattern) {
                if (PatternLockUtils.patternToSha1(patternLockView, pattern).equals(SharedPreferencesHelper.getString(context, DEFAULT_PASSWORD_KEY, null))) {
                    dialog.dismiss();
                } else {
                    patternLockView.setViewMode(PatternLockView.PatternViewMode.WRONG);
                    android.os.Handler handler = new android.os.Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            patternLockView.clearPattern();
                        }
                    }, CLEAR_PATTERN_DELAY);
                }
            }

            @Override
            public void onCleared() {

            }
        });

        dialog = new Dialog(context, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        } else {
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setContentView(promptsView);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.setOnKeyListener(dialogKeyListener);
        dialog.show();
    }

    private Dialog.OnKeyListener dialogKeyListener = new Dialog.OnKeyListener() {
        @Override
        public boolean onKey(DialogInterface dialog, int keyCode,
                             KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK
                    && event.getAction() == KeyEvent.ACTION_UP) {
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);
                dialog.dismiss();
            }
            return true;
        }
    };

    private Runnable showDialogRunnable = new Runnable() {
        @Override
        public void run() {
            showDialog();
        }
    };

    public static void startService(Context context) {
        Intent intent = new Intent(context, ServiceChecking.class);
        context.startService(intent);
    }
}
