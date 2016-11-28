package com.pefe.pefememo.memo.rootservice;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.WindowManager;

import com.pefe.pefememo.PefeMemo;
import com.pefe.pefememo.R;
import com.pefe.pefememo.app.main.MainViewImpl;
import com.pefe.pefememo.lockscreen.LockScreenViewImpl;
import com.pefe.pefememo.memo.memo.MemoView;
import com.pefe.pefememo.memo.memo.MemoViewImpl;
import com.pefe.pefememo.preference.PreferenceControl;
import com.pefe.pefememo.preference.PreferenceControlImpl;

/*
* RootService는 메모와 잠금화면의 루트가 되는 서비스이다.
*  * 메모와 잠금화면을 사용할 경우 이 서비스가 테스크 킬러로부터 안전하기 위해 Notification을 가지고 있다.
*  * 메모를 열기 위한 동작을 감지하는 투명 버튼을 가지고 있다
*  * 메모의 사이즈를 정하기 위해 화면 크기를 측정한다.
*  * 잠금화면을 열기 위한 BroadCastReceiver를 가지고 있다.
*
* */
public class RootService extends Service {

    private PreferenceControl preferenceControl;
    private MemoView memoView;
    private ScreenOnOffReciever screenOnOffReciever;


    public static boolean memoUse;
    public static boolean lockScreenUse;

    private Notification noti =null;

    private float widthPixel;
    private float heightPixel;

    public RootService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setPreferenceControl();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        PefeMemo.setIsRootOn(true);
        setPixels();
        setViewBuilders();
        setSettings();
        registerScreenOnOffReciever();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        memoView.terminateMemo();
        unregisterReceiver(screenOnOffReciever);
        stopForeground(true);
        PefeMemo.setIsRootOn(false);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    //프리퍼런스 사용 세팅
    private void setPreferenceControl(){
        SharedPreferences preferences = getSharedPreferences(PreferenceControlImpl.SAVE_SETTINGS,0);
        preferenceControl = new PreferenceControlImpl(preferences);
        preferences.registerOnSharedPreferenceChangeListener(new OnStatusChangedListener());
    }
    //화면 픽셀 크기 즉정
    private void setPixels(){
        widthPixel = getResources().getDisplayMetrics().widthPixels;
        heightPixel = getResources().getDisplayMetrics().heightPixels;
    }
    //View를 그리기위해 필요한 윈도우 매니저와 인플레이터를 MemoView클래스에 전달함
    private void setViewBuilders(){
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        memoView = new MemoViewImpl(this,wm,inflater,heightPixel,widthPixel);
        memoView.initMemo();
    }

    // 환경설정 반영하기
    private void setSettings(){
        memoUse = preferenceControl.restoreMemoUse();
        lockScreenUse = preferenceControl.restoreLockScreenUse();
        //Memo와 lockScreenUse의 변화마다 notification 사용 여부 변경
        onOffNotification();
    }
    private void registerScreenOnOffReciever(){
        screenOnOffReciever = new ScreenOnOffReciever();
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(screenOnOffReciever, filter);
    }
    //Notification 달기
    private static final int NOTI_ID = 3223;
    private void onOffNotification(){
        if (noti == null) {
            if(memoUse||lockScreenUse){
                noti = makeNotification();
                startForeground(NOTI_ID,noti);
            }
        }else{
            if(memoUse||lockScreenUse){
                startForeground(NOTI_ID,noti);
            }else{
                stopForeground(true);
                noti = null;
            }
        }

    }
    private final int NOTI_REQUEST = 8080;
    private Notification makeNotification(){
        // 노티 클릭시 앱으로 가기 위한 인텐트
        Intent intent = new Intent(this,MainViewImpl.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,NOTI_REQUEST,intent,0);
        Bitmap largeIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.pflargeicon);
        Notification.Builder builder = new Notification.Builder(this).setContentTitle("Click to go Pefe App")
                .setSmallIcon(R.drawable.pefenotification)
                .setLargeIcon(largeIcon)
                .setOngoing(true)
                .setContentIntent(pendingIntent);
        Notification result = builder.build();
        return result;
    }



    //어플리케이션에서 환경설정을 변화에 대한 리스너
    private class OnStatusChangedListener implements SharedPreferences.OnSharedPreferenceChangeListener{
        //변경시 사용여부 업데이트
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
            setSettings();
        }
    }
    // 스크린 온오프 감지 브로드캐스트 리시버
    private class ScreenOnOffReciever extends BroadcastReceiver {
        boolean phoneCallStatus = false;
        TelephonyManager telephonyManager = null;

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (telephonyManager == null) {
                telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
            }
            if (lockScreenUse) {
                if (action.equals(Intent.ACTION_SCREEN_OFF) && !phoneCallStatus) {
                    Intent lockScreenIntent = new Intent(RootService.this, LockScreenViewImpl.class);
                    lockScreenIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    lockScreenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(lockScreenIntent);
                }
            }
        }
        // 핸드폰이 통화중일 경우 반응하지 않도록 설정
        private PhoneStateListener phoneListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                switch (state) {
                    //전화 올 때
                    case TelephonyManager.CALL_STATE_RINGING:
                        phoneCallStatus = true;
                        break;
                    //전화 받음
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        phoneCallStatus = true;
                        break;
                    //전화 끊음
                    case TelephonyManager.CALL_STATE_IDLE:
                        phoneCallStatus = false;
                        break;
                }
            }
        };
    }
}
