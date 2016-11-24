package com.pefe.pefememo.preference;


public interface PreferenceControl {
    void saveMemoUse(boolean memoUse);
    void saveLockScreenUse(boolean lockScreenUse);
    boolean restoreMemoUse();
    boolean restoreLockScreenUse();

}
