package com.thepyramid.appslocker.locker;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.thepyramid.appslocker.common.Base.BaseFragment;
import com.thepyramid.appslocker.common.Helpers.SharedPreferencesHelper;
import com.thepyramid.appslocker.common.ServiceChecking;
import com.thepyramid.appslocker.R;

import java.util.List;

import static com.thepyramid.appslocker.common.Helpers.SharedPreferencesHelper.DEFAULT_PASSWORD_KEY;
import static com.thepyramid.appslocker.common.Helpers.SharedPreferencesHelper.PIN;


public class FragmentLocker extends BaseFragment implements LockerView {

    private String patternString = null;
    private static final String PATTERN_STRING = "PATTERN_STRING";
    private PatternLockView plvHomePattern;
    private TextView txtDefaultLock;
    private RelativeLayout rlProgress;
    private boolean locked = false;
    private Context context;
    private LockerPresenter lockerPresenter;
    private Dialog dialogPin;
    private LayoutInflater layoutInflater;


    private TextView txtEnterYourPin;
    private EditText edtPinCode;
    private Button btnSubmit;

    public FragmentLocker() {
        // Required empty public constructor
    }

    public static FragmentLocker newInstance() {
        FragmentLocker fragment = new FragmentLocker();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_locker, container, false);
        context = getActivity();
        initializeViews(view);
        openLockOrSetLock();
        setListeners();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!locked) {
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.dialog_enter_pin, null, false);
            findViewsInPinDialog(view);
            AlertDialog.Builder b = new AlertDialog.Builder(context);
            b.setView(view);
            dialogPin = b.create();
            dialogPin.setCanceledOnTouchOutside(false);
            dialogPin.setCancelable(false);
            dialogPin.show();
        }
        lockerPresenter = new LockerPresenter(this);
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(PATTERN_STRING)) {
                patternSetSuccessfullyProceedToConfirm(savedInstanceState.getString(PATTERN_STRING));
            }
        }
    }

    private void findViewsInPinDialog(View view) {
        txtEnterYourPin = view.findViewById(R.id.txtEnterYourPin);
        txtEnterYourPin.setText(R.string.pleaseEnterPinItIsRequiredInCaseOfYouForgbetThePattern);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        edtPinCode = view.findViewById(R.id.edtPinCode);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(edtPinCode.getText().toString().trim())) {
                    Toast.makeText(context, R.string.youHaveToEnterPin, Toast.LENGTH_LONG).show();
                } else {
                    dialogPin.dismiss();
                    SharedPreferencesHelper.addString(context, PIN, edtPinCode.getText().toString());
                }
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (patternString != null) {
            outState.putString(PATTERN_STRING, patternString);
        }
    }

    @Override
    protected void initializeViews(View v) {
        plvHomePattern = (PatternLockView) v.findViewById(R.id.plvHomePattern);
        txtDefaultLock = (TextView) v.findViewById(R.id.txtDefaultLock);
        rlProgress = v.findViewById(R.id.rlProgress);
    }

    private void openLockOrSetLock() {
        if (SharedPreferencesHelper.hasValue(context, DEFAULT_PASSWORD_KEY)) {
            txtDefaultLock.setText(getString(R.string.drawYourPattern));
            locked = true;
        } else {
            locked = false;
        }
        ServiceChecking.startService(context);
    }

    @Override
    protected void setListeners() {
        plvHomePattern.addPatternLockListener(patternLockViewListener);
    }

    private PatternLockViewListener patternLockViewListener = new PatternLockViewListener() {
        @Override
        public void onStarted() {

        }

        @Override
        public void onProgress(List<PatternLockView.Dot> progressPattern) {

        }

        @Override
        public void onComplete(List<PatternLockView.Dot> pattern) {
            if (patternString == null)
                lockerPresenter.lockComplete(context, pattern, locked, plvHomePattern);
            else
                lockerPresenter.lockCompleteForConfirmation(context, pattern, plvHomePattern, patternString);
        }

        @Override
        public void onCleared() {

        }
    };

    @Override
    public void patternWrong() {
        plvHomePattern.setViewMode(PatternLockView.PatternViewMode.WRONG);
    }

    @Override
    public void patternSetSuccessfullyProceedToConfirm(String pattern) {
        this.patternString = pattern;
        txtDefaultLock.setText(R.string.confirmYourPattern);
        plvHomePattern.clearPattern();
    }

    @Override
    public void showHideProgress(boolean show) {
        if (show)
            rlProgress.setVisibility(View.VISIBLE);
        else
            rlProgress.setVisibility(View.GONE);
    }
}
