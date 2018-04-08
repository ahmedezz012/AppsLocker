package com.thepyramid.appslocker.changePattern;

/**
 * Created by samar ezz on 4/6/2018.
 */

public interface ChangePatternView {

    void patternWrong();
    void currentPatternIsCorrectProceedToEnterNew(String currentPattern);
    void newPatternIsCorrectProceedToConfirm(String newPattern);
    void confirmPatternIsCorrectProceedToSaveIt(String confirmPattern);
    void showHideProgress(boolean show);
}
