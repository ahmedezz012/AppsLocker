package com.thepyramid.appslocker.changePattern;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.thepyramid.appslocker.common.Helpers.SharedPreferencesHelper;
import com.thepyramid.appslocker.R;
import com.thepyramid.appslocker.common.Helpers.Utils;
import com.thepyramid.appslocker.common.ServiceChecking;

import java.util.List;

import static com.thepyramid.appslocker.common.Helpers.SharedPreferencesHelper.DEFAULT_PASSWORD_KEY;
import static com.thepyramid.appslocker.common.Helpers.SharedPreferencesHelper.USER_ID;
import static com.thepyramid.appslocker.locker.LockerPresenter.CLEAR_PATTERN_DELAY;
import static com.thepyramid.appslocker.locker.LockerPresenter.MINIMUM_PATTERN_SIZE;

/**
 * Created by samar ezz on 4/6/2018.
 */

public class ChangePatternPresenter {
    private ChangePatternView changePatternView;
    private Context context;
    private String newPattern, newPatternString;


    public ChangePatternPresenter(Context context, ChangePatternView changePatternView) {
        this.changePatternView = changePatternView;
        this.context = context;
    }

    public void lockCompleteForCurrentPattern(List<PatternLockView.Dot> pattern, final PatternLockView plvChangePattern) {
        if (PatternLockUtils.patternToSha1(plvChangePattern, pattern).equals(SharedPreferencesHelper.getString(context, DEFAULT_PASSWORD_KEY, null))) {
            changePatternView.currentPatternIsCorrectProceedToEnterNew(PatternLockUtils.patternToSha1(plvChangePattern, pattern));
        } else {
            wrongPattern(plvChangePattern);
        }
    }

    public void lockCompleteForNewPattern(List<PatternLockView.Dot> pattern, PatternLockView plvChangePattern) {
        if (pattern.size() < MINIMUM_PATTERN_SIZE) {
            Toast.makeText(context, context.getString(R.string.yourPatternMustBeThree), Toast.LENGTH_LONG).show();
            wrongPattern(plvChangePattern);
        } else {
            changePatternView.newPatternIsCorrectProceedToConfirm(PatternLockUtils.patternToSha1(plvChangePattern, pattern));
        }
    }

    private void wrongPattern(final PatternLockView plvChangePattern) {
        changePatternView.patternWrong();
        android.os.Handler handler = new android.os.Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                plvChangePattern.clearPattern();
            }
        }, CLEAR_PATTERN_DELAY);
    }

    public void lockCompleteForConfirmPattern(List<PatternLockView.Dot> pattern, final PatternLockView plvChangePattern, String newPattern) {
        if (PatternLockUtils.patternToSha1(plvChangePattern, pattern).equals(newPattern)) {
            this.newPattern = newPattern;
            newPatternString = pattern.toString();
            AddPatternToFireBaseTask addPatternToFireBaseTask = new AddPatternToFireBaseTask();
            addPatternToFireBaseTask.execute();
            android.os.Handler handler = new android.os.Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    plvChangePattern.clearPattern();
                }
            }, CLEAR_PATTERN_DELAY);
        } else {
            Toast.makeText(context, context.getString(R.string.theNewPatternAndConfirmationNotMatch), Toast.LENGTH_LONG).show();
            wrongPattern(plvChangePattern);
        }
    }

    private class AddPatternToFireBaseTask extends AsyncTask<Void, Void, Boolean> {
        private FirebaseDatabase firebaseDatabase;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            firebaseDatabase = FirebaseDatabase.getInstance();
            changePatternView.showHideProgress(true);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            final Boolean[] returnedValue = new Boolean[1];
            String userId = SharedPreferencesHelper.getString(context, USER_ID, "");
            firebaseDatabase.getReference(userId).child(Utils.USER_PATTERN).setValue(newPatternString).addOnSuccessListener(new OnSuccessListener<Void>() {
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
            changePatternView.showHideProgress(false);
            SharedPreferencesHelper.addString(context, DEFAULT_PASSWORD_KEY, newPattern);
            changePatternView.confirmPatternIsCorrectProceedToSaveIt(newPattern);
        }
    }
}
