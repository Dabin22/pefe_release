package com.pefe.pefememo.app.fragments.setting;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.pefe.pefememo.PefeMemo;
import com.pefe.pefememo.R;
import com.pefe.pefememo.app.main.MainViewImpl;
import com.pefe.pefememo.memo.rootservice.RootService;
import com.pefe.pefememo.preference.PreferenceControl;
import com.pefe.pefememo.preference.PreferenceControlImpl;

public class SettingsFragment extends Fragment {
    PreferenceControl preferenceControl;

    Switch switchMemo, switchLockScreen;
    public SettingsFragment() {}

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = getActivity().getSharedPreferences(PreferenceControlImpl.SAVE_SETTINGS,0);
        preferenceControl = new PreferenceControlImpl(preferences);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View settingFragment = inflater.inflate(R.layout.fragment_settings, container, false);
        switchMemo =(Switch)settingFragment.findViewById(R.id.switchMemo);
        switchMemo.setChecked(preferenceControl.restoreMemoUse());
        switchMemo.setOnCheckedChangeListener( new CheckChangeListener());
        switchLockScreen =(Switch)settingFragment.findViewById(R.id.switchLockScreen);
        switchLockScreen.setChecked(preferenceControl.restoreLockScreenUse());
        switchLockScreen.setOnCheckedChangeListener(new CheckChangeListener());

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

    private void checkRoot(){
        Intent intent = new Intent(getActivity(), RootService.class);
        if(switchMemo.isChecked() || switchLockScreen.isChecked()){
            if(!PefeMemo.isRootOn()){
                getActivity().startService(intent);
            }
        }else{
            if(PefeMemo.isRootOn()){
                getActivity().stopService(intent);
            }
        }
    }
    private class CheckChangeListener implements CompoundButton.OnCheckedChangeListener{
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if(compoundButton.equals(switchMemo)){
                try {
                    preferenceControl.saveMemoUse(b);
                }catch (Exception e){e.printStackTrace();}

            }else if(compoundButton.equals(switchLockScreen)){
                try {
                    preferenceControl.saveLockScreenUse(b);
                }catch (Exception e){e.printStackTrace();}
            }
            checkRoot();
        }
    }
}
