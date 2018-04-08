package com.thepyramid.appslocker.locker;

/**
 * Created by samar ezz on 8/11/2017.
 */

public interface LockerView{

    void patternWrong();
    void patternSetSuccessfullyProceedToConfirm(String pattern);
    void showHideProgress(boolean show);
}
