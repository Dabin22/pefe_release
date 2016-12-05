package com.pefe.pefememo.app.fragments.setting;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.pefe.pefememo.PefeMemo;
import com.pefe.pefememo.R;
import com.pefe.pefememo.app.main.MainViewImpl;
import com.pefe.pefememo.memo.rootservice.RootService;
import com.pefe.pefememo.preference.PreferenceControl;
import com.pefe.pefememo.preference.PreferenceControlImpl;

public class SettingsFragment extends Fragment {
    private PreferenceControl preferenceControl;
    private SharedPreferences preferences;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    private Button switchMemo, switchLockScreen;
    public SettingsFragment() {}

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getActivity().getSharedPreferences(PreferenceControlImpl.SAVE_SETTINGS,0);
        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                setSwitchStatus();
            }
        };
        preferenceControl = new PreferenceControlImpl(preferences);
        preferences.registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View settingFragment = inflater.inflate(R.layout.fragment_settings, container, false);
        switchMemo =(Button)settingFragment.findViewById(R.id.switchMemo);
        switchLockScreen =(Button)settingFragment.findViewById(R.id.switchLockScreen);
        setSwitchStatus();
        switchMemo.setOnClickListener(new SwitchClickListener());
        switchLockScreen.setOnClickListener(new SwitchClickListener());
        return settingFragment;
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
    public void onDestroy() {
        super.onDestroy();
        preferences.unregisterOnSharedPreferenceChangeListener(listener);

    }
    private void setSwitchStatus(){
        if(preferenceControl.restoreMemoUse()) {
            switchMemo.setBackgroundResource(R.drawable.btn_memo_on);
        }else{
            switchMemo.setBackgroundResource(R.drawable.btn_memo_off);
        }
        if(preferenceControl.restoreLockScreenUse()){
            switchLockScreen.setBackgroundResource(R.drawable.btn_lock_on);
        }else{
            switchLockScreen.setBackgroundResource(R.drawable.btn_lock_off);
        }
    }

    private void checkRoot(){
        Intent intent = new Intent(getActivity(), RootService.class);
        if(!PefeMemo.isRootOn()){
            getActivity().startService(intent);
        }
//        if(switchMemo.isChecked() || switchLockScreen.isChecked()){
//            if(!PefeMemo.isRootOn()){
//                getActivity().startService(intent);
//            }
//        }else{
//            if(PefeMemo.isRootOn()){
//                getActivity().stopService(intent);
//            }
//        }
    }
    private class SwitchClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            if(view.equals(switchMemo)){
                try {
                    boolean memoS = !preferenceControl.restoreMemoUse();
                    preferenceControl.saveMemoUse(memoS);
                }catch (Exception e){e.printStackTrace();}

            }else if(view.equals(switchLockScreen)){
                try {
                    boolean lockS = !preferenceControl.restoreLockScreenUse();
                    preferenceControl.saveLockScreenUse(lockS);
                }catch (Exception e){e.printStackTrace();}
            }
            checkRoot();
        }
    }
}
