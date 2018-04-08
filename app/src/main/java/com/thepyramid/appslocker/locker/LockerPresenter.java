package com.thepyramid.appslocker.locker;

import android.app.ActivityOptions;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.thepyramid.appslocker.apps.ActivityApps;
import com.thepyramid.appslocker.common.Helpers.SharedPreferencesHelper;
import com.thepyramid.appslocker.R;
import com.thepyramid.appslocker.common.Helpers.Utils;
import com.thepyramid.appslocker.common.ServiceChecking;

import java.util.Calendar;
import java.util.List;

import static com.thepyramid.appslocker.common.Helpers.SharedPreferencesHelper.DEFAULT_PASSWORD_KEY;
import static com.thepyramid.appslocker.common.Helpers.SharedPreferencesHelper.USER_ID;

/**
 * Created by samar ezz on 8/11/2017.
 */

public class LockerPresenter {
    private LockerView lockerView;
    public static final int MINIMUM_PATTERN_SIZE = 3;
    public static final long CLEAR_PATTERN_DELAY = 200;
    private String userId = null;
    private String userPattern = null;
    private Context context;

    public LockerPresenter(LockerView lockerView) {
        this.lockerView = lockerView;
    }

    public void lockComplete(Context context, List<PatternLockView.Dot> pattern, boolean locked, final PatternLockView plvHomePattern) {
        if (locked) {
            if (PatternLockUtils.patternToSha1(plvHomePattern, pattern).equals(SharedPreferencesHelper.getString(context, DEFAULT_PASSWORD_KEY, null))) {
                openActivityAppsScreen(context);
            } else {
                lockerView.patternWrong();
                android.os.Handler handler = new android.os.Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        plvHomePattern.clearPattern();
                    }
                }, CLEAR_PATTERN_DELAY);
            }
        } else {
            if (pattern.size() < MINIMUM_PATTERN_SIZE) {
                Toast.makeText(context, context.getString(R.string.yourPatternMustBeThree), Toast.LENGTH_LONG).show();
                lockerView.patternWrong();
                android.os.Handler handler = new android.os.Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        plvHomePattern.clearPattern();
                    }
                }, CLEAR_PATTERN_DELAY);
            } else {
                lockerView.patternSetSuccessfullyProceedToConfirm(PatternLockUtils.patternToSha1(plvHomePattern, pattern));
            }
        }
    }

    public void lockCompleteForConfirmation(Context context, List<PatternLockView.Dot> pattern, final PatternLockView plvHomePattern, String patternString) {
        this.context = context;
        if (PatternLockUtils.patternToSha1(plvHomePattern, pattern).equals(patternString)) {
            userPattern = pattern.toString();
            userId = String.valueOf(Calendar.getInstance().getTimeInMillis());
            SharedPreferencesHelper.addString(context, USER_ID, userId);
            SharedPreferencesHelper.addString(context, DEFAULT_PASSWORD_KEY, PatternLockUtils.patternToSha1(plvHomePattern, pattern));
            AddPatternToFireBaseTask addPatternToFireBaseTask = new AddPatternToFireBaseTask();
            addPatternToFireBaseTask.execute();
        } else {
            Toast.makeText(context, context.getString(R.string.thePatternAndConfirmationNotMatch), Toast.LENGTH_LONG).show();
            lockerView.patternWrong();
        }
        android.os.Handler handler = new android.os.Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                plvHomePattern.clearPattern();
            }
        }, CLEAR_PATTERN_DELAY);
    }

    private void openActivityAppsScreen(Context context) {
        if (Utils.isLollipop()) {
            ActivityApps.openActivityApps(context, ActivityOptions.makeSceneTransitionAnimation((AppCompatActivity) context).toBundle());
        } else {
            ActivityApps.openActivityApps(context);
        }
    }

    private class AddPatternToFireBaseTask extends AsyncTask<Void, Void, Boolean> {
        private FirebaseDatabase firebaseDatabase;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            firebaseDatabase = FirebaseDatabase.getInstance();
            lockerView.showHideProgress(true);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            final Boolean[] returnedValue = new Boolean[1];
            firebaseDatabase.getReference(userId).child(Utils.USER_PATTERN).setValue(userPattern).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    returnedValue[0] = true;
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, R.string.addingYourPatternToTheServerFailed, Toast.LENGTH_LONG).show();
                    returnedValue[0] = false;
                }
            });
            return returnedValue[0];
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            openActivityAppsScreen(context);
            ServiceChecking.startService(context);
            lockerView.showHideProgress(false);
        }
    }
}
