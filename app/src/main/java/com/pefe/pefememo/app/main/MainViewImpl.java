package com.pefe.pefememo.app.main;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.pefe.pefememo.PefeMemo;
import com.pefe.pefememo.R;
import com.pefe.pefememo.app.fragments.memo.MemoFragment;
import com.pefe.pefememo.app.fragments.setting.SettingsFragment;
import com.pefe.pefememo.app.fragments.todo.TodoFragment;
import com.pefe.pefememo.memo.rootservice.RootService;
import com.pefe.pefememo.model.directory.Directory;
import com.pefe.pefememo.model.memo.Memo;
import com.pefe.pefememo.model.todo.Todo;
import com.pefe.pefememo.preference.PreferenceControl;
import com.pefe.pefememo.preference.PreferenceControlImpl;
import com.pefe.pefememo.realm.RealmController;
import com.pefe.pefememo.sample.Sample;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainViewImpl extends AppCompatActivity {




    TabLayout tabs;
    ViewPager viewPager;

    ArrayList<Fragment> fragments = new ArrayList<>();
    MemoFragment memoFragment;
    TodoFragment todoFragment;
    SettingsFragment settingsFragment;

    RealmController realmController;
    PreferenceControl preferenceControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_view);
        SharedPreferences preferences = getSharedPreferences(PreferenceControlImpl.SAVE_SETTINGS,0);
        preferenceControl = new PreferenceControlImpl(preferences);
        versionCheck();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private void init(){
        drawViews();
        checkRoot();
    }

    private void drawViews(){
        //메모와 잠금화면 사용 여부 설정 스위치

        tabs = (TabLayout) findViewById(R.id.tabs);
        memoFragment = MemoFragment.newInstance();
        todoFragment = TodoFragment.newInstance();
        settingsFragment = SettingsFragment.newInstance();
        fragments.add(memoFragment);
        fragments.add(todoFragment);
        fragments.add(settingsFragment);
        viewPager=(ViewPager)findViewById(R.id.viewPager);
        setupViewPager(viewPager,fragments,getSupportFragmentManager());
        tabs.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));
    }

    private void checkRoot(){
        Intent intent = new Intent(MainViewImpl.this, RootService.class);
//        if(preferenceControl.restoreMemoUse()|| preferenceControl.restoreLockScreenUse()){
            if(!PefeMemo.isRootOn()){
                startService(intent);
            }
//        }else{
//            if(PefeMemo.isRootOn()){
//                stopService(intent);
//            }
//        }
    }

    private void setupViewPager(ViewPager viewPager, ArrayList<Fragment> fragments, FragmentManager fragmentManager){
        CustomPagerAdapter adapter = new CustomPagerAdapter( fragments, fragmentManager);
        viewPager.setAdapter(adapter);
    }


    //퍼미션
    private void versionCheck(){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            init();} else {checkPermissions();}
    }

    private final int PERMISSION_REQUEST_CODE =333;
    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermissions() {
        //출저 http://stackoverflow.com/questions/32652533/android-system-overlay-window
        if (!android.provider.Settings.canDrawOverlays(this)) {
            Intent permission_request = new Intent(android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(permission_request,PERMISSION_REQUEST_CODE);
        } else if(android.provider.Settings.canDrawOverlays(this)){
            checkPermissionList();
        }
    }
    @TargetApi(Build.VERSION_CODES.M)
    public void checkPermissionList() {
        //REQUESTCODE를 사용한다. ->후에 onReuqestPermissionResult라는 함수를 override해야한다.
        if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            String permissionArray[] = {android.Manifest.permission.READ_PHONE_STATE};
            requestPermissions(permissionArray, PERMISSION_REQUEST_CODE);

        } else {
            init();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSION_REQUEST_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                init();
            }

        }

    }
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PERMISSION_REQUEST_CODE){
            if(android.provider.Settings.canDrawOverlays(this)){
                this.init();
            }
        }
    }
}
