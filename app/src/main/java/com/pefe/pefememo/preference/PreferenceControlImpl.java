package com.pefe.pefememo.preference;

import android.content.SharedPreferences;

/*
  * 환경설정사항을 Preference에 저장하는 역할을 담당함
  * 현재 메모 사용여부, 잠금화면 사용여부를 Preference에 저장함
 */
public class PreferenceControlImpl implements PreferenceControl{

    private SharedPreferences preferences = null;
    public static final String FIRST ="FIRST";
    public static final String SAVE_SETTINGS = "SAVE_SETTINGS";
    private final String MEMO_USE ="MEMO_USE";
    private final String LOCKSCREEN_USE ="LOCKSCREEN_USE";
    public PreferenceControlImpl(SharedPreferences preferences){this.preferences = preferences;}

    @Override
    public boolean isFirst() {
        boolean result;
        result = preferences.getBoolean(FIRST,true);
        return result;
    }

    @Override
    public void notFirst() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(FIRST,false);
        editor.apply();
    }

    @Override
    public void saveMemoUse(boolean memoUse){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(MEMO_USE, memoUse);
        editor.apply();
    }
    @Override
    public void saveLockScreenUse(boolean lockScreenUse){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(LOCKSCREEN_USE,lockScreenUse);
        editor.apply();
    }

    @Override
    public boolean restoreMemoUse(){
        boolean result;
        result = preferences.getBoolean(MEMO_USE,true);
        return result;
    }
    @Override
    public boolean restoreLockScreenUse(){
        boolean result;
        result = preferences.getBoolean(LOCKSCREEN_USE, true);
        return result;
    }

}
