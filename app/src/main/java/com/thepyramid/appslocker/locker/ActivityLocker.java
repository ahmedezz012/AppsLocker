package com.thepyramid.appslocker.locker;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thepyramid.appslocker.changePattern.ChangePatternActivity;
import com.thepyramid.appslocker.common.Base.BaseActivity;
import com.thepyramid.appslocker.common.Helpers.SharedPreferencesHelper;
import com.thepyramid.appslocker.common.Helpers.Utils;
import com.thepyramid.appslocker.R;
import com.thepyramid.appslocker.lockedApps.LockedAppsActivity;

import static com.thepyramid.appslocker.common.Helpers.SharedPreferencesHelper.PIN;
import static com.thepyramid.appslocker.common.Helpers.SharedPreferencesHelper.USER_ID;
import static com.thepyramid.appslocker.common.Helpers.Utils.USER_PATTERN;


public class ActivityLocker extends BaseActivity {


    private static final String FRAGMENT_LOCKER_TAG = "FRAGMENT_LOCKER_TAG";
    private Toolbar toolBar;
    private FirebaseDatabase firebaseDatabase;
    private TextView txtEnterYourPin;
    private EditText edtPinCode;
    private Button btnSubmit;
    private Dialog dialogPin;
    private FragmentLocker fragmentLocker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViews();
        firebaseDatabase = FirebaseDatabase.getInstance();
        setToolbar(toolBar, getString(R.string.app_name), false);
        if (savedInstanceState == null)
            loadFragment(FragmentLocker.newInstance());
        else {
            fragmentLocker = (FragmentLocker) getSupportFragmentManager().findFragmentByTag(FRAGMENT_LOCKER_TAG);
        }
        Utils.requestUsageStatsPermission(this);
    }

    @Override
    protected void initializeViews() {
        toolBar = findViewById(R.id.toolBar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.more_menu_forget_pattern, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuForgetPattern:
                if (SharedPreferencesHelper.hasValue(ActivityLocker.this, SharedPreferencesHelper.DEFAULT_PASSWORD_KEY))
                    showForgetPatternDialog();
                else {
                    Toast.makeText(ActivityLocker.this, R.string.youDontHavePatternYet, Toast.LENGTH_LONG).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showForgetPatternDialog() {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.dialog_enter_pin, null, false);
        findViewsInPinDialog(view);
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setView(view);
        dialogPin = b.create();
        dialogPin.setCanceledOnTouchOutside(false);
        dialogPin.setCancelable(false);
        dialogPin.show();
    }

    private void findViewsInPinDialog(View view) {
        txtEnterYourPin = view.findViewById(R.id.txtEnterYourPin);
        txtEnterYourPin.setText(R.string.pleaseEnterPinToSendYouThePattern);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        edtPinCode = view.findViewById(R.id.edtPinCode);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(edtPinCode.getText().toString().trim())) {
                    Toast.makeText(ActivityLocker.this, R.string.youHaveToEnterPin, Toast.LENGTH_LONG).show();
                } else {
                    if (edtPinCode.getText().toString()
                            .equals(SharedPreferencesHelper.getString(ActivityLocker.this, PIN, ""))) {
                        fragmentLocker.showHideProgress(true);
                        dialogPin.dismiss();
                        firebaseDatabase.getReference(SharedPreferencesHelper.getString(ActivityLocker.this, USER_ID, "")).child(USER_PATTERN)
                                .addListenerForSingleValueEvent(valueEventListener);
                    } else {
                        Toast.makeText(ActivityLocker.this, R.string.wrongPin, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        firebaseDatabase.getReference(SharedPreferencesHelper.getString(ActivityLocker.this, USER_ID, "")).child(USER_PATTERN).removeEventListener(valueEventListener);
    }

    private void loadFragment(Fragment fragment) {
        fragmentLocker = (FragmentLocker) fragment;
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frmFragmentContainer, fragment, FRAGMENT_LOCKER_TAG);
        fragmentTransaction.commit();
    }

    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            fragmentLocker.showHideProgress(false);
            if (dataSnapshot != null) {
                String pattern = dataSnapshot.getValue(String.class);
                if (!TextUtils.isEmpty(pattern))
                    Toast.makeText(ActivityLocker.this,
                            String.format(getString(R.string.yourPatternIs), pattern),
                            Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(ActivityLocker.this, R.string.errorInGetPatternTryAgainLater, Toast.LENGTH_LONG).show();
            } else
                Toast.makeText(ActivityLocker.this, R.string.errorInGetPatternTryAgainLater, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            fragmentLocker.showHideProgress(false);
            Toast.makeText(ActivityLocker.this, R.string.errorInGetPatternTryAgainLater, Toast.LENGTH_LONG).show();
        }
    };
}
