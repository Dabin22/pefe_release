package com.pefe.pefememo.preference;


public interface PreferenceControl {
    boolean isFirst();
    void notFirst();
    void saveMemoUse(boolean memoUse);
    void saveLockScreenUse(boolean lockScreenUse);
    boolean restoreMemoUse();
    boolean restoreLockScreenUse();

}
