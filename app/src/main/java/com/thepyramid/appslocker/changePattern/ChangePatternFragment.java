package com.thepyramid.appslocker.changePattern;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.thepyramid.appslocker.common.Base.BaseFragment;
import com.thepyramid.appslocker.R;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChangePatternFragment extends BaseFragment implements ChangePatternView {


    private LinearLayout lnrChangePattern;
    private TextView txtMessage;
    private PatternLockView plvChangePattern;
    private String currentPattern = null, newPattern = null, confirmPattern = null;
    private static final String CURRENT_PATTERN = "CURRENT_PATTERN", NEW_PATTERN = "NEW_PATTERN";
    private ChangePatternPresenter changePatternPresenter;
    private Context context;
    private RelativeLayout rlProgress;

    public ChangePatternFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_change_pattern, container, false);
        context = getActivity();
        initializeViews(v);
        setListeners();
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        txtMessage.setText(R.string.enterYourCurrentPattern);
        lnrChangePattern.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.changePatternOld, null));
        changePatternPresenter = new ChangePatternPresenter(context, this);
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(CURRENT_PATTERN)) {
                currentPattern = savedInstanceState.getString(CURRENT_PATTERN);
                if (savedInstanceState.containsKey(NEW_PATTERN)) {
                    newPattern = savedInstanceState.getString(NEW_PATTERN);
                    setConfirmPatternView();
                } else {
                    setNewPatternView();
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (currentPattern != null) {
            outState.putString(CURRENT_PATTERN, currentPattern);
            if (newPattern != null) {
                outState.putString(NEW_PATTERN, newPattern);
            }
        }
    }

    @Override
    protected void initializeViews(View v) {
        rlProgress = v.findViewById(R.id.rlProgress);
        lnrChangePattern = v.findViewById(R.id.lnrChangePattern);
        txtMessage = v.findViewById(R.id.txtMessage);
        plvChangePattern = v.findViewById(R.id.plvChangePattern);
    }

    @Override
    protected void setListeners() {
        plvChangePattern.addPatternLockListener(patternLockViewListener);
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
            if (currentPattern == null)
                changePatternPresenter.lockCompleteForCurrentPattern(pattern, plvChangePattern);
            else if (newPattern == null) {
                changePatternPresenter.lockCompleteForNewPattern(pattern, plvChangePattern);
            } else if (confirmPattern == null) {
                changePatternPresenter.lockCompleteForConfirmPattern(pattern, plvChangePattern, newPattern);
            }
        }

        @Override
        public void onCleared() {

        }
    };

    public static ChangePatternFragment newInstance() {
        return new ChangePatternFragment();
    }

    @Override
    public void patternWrong() {
        plvChangePattern.setViewMode(PatternLockView.PatternViewMode.WRONG);
    }

    @Override
    public void currentPatternIsCorrectProceedToEnterNew(String currentPattern) {
        setNewPatternView();
        this.currentPattern = currentPattern;
        plvChangePattern.clearPattern();
    }

    @Override
    public void newPatternIsCorrectProceedToConfirm(String newPattern) {
        this.newPattern = newPattern;
        setConfirmPatternView();
        plvChangePattern.clearPattern();
    }

    private void setNewPatternView() {
        txtMessage.setText(R.string.enterYourNewPattern);
        lnrChangePattern.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.changePatternNew, null));
    }

    private void setConfirmPatternView() {
        txtMessage.setText(R.string.confirmYourPattern);
        lnrChangePattern.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.changePatternConfirm, null));
    }

    @Override
    public void confirmPatternIsCorrectProceedToSaveIt(String confirmPattern) {
        this.confirmPattern = confirmPattern;
        Toast.makeText(context, context.getString(R.string.patternChangedSuccessfully), Toast.LENGTH_LONG).show();
        ((AppCompatActivity) context).finish();
    }

    @Override
    public void showHideProgress(boolean show) {
        if (show)
            rlProgress.setVisibility(View.VISIBLE);
        else
            rlProgress.setVisibility(View.GONE);
    }

}
