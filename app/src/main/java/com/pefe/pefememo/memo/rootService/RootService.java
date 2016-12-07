package com.pefe.pefememo.memo.rootservice;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.RemoteViews;

import com.pefe.pefememo.PefeMemo;
import com.pefe.pefememo.R;
import com.pefe.pefememo.memo.lockscreen.LockScreenView;
import com.pefe.pefememo.memo.lockscreen.LockScreenViewImpl;
import com.pefe.pefememo.realm.RealmController;
import com.pefe.pefememo.realm.RealmControllerImpl;
import com.pefe.pefememo.app.main.MainViewImpl;
import com.pefe.pefememo.memo.memo.MemoController;
import com.pefe.pefememo.memo.memo.MemoCotrollerImpl;
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
    private RealmController realmController;
    private MemoController memoController;
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
        setControllers();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        PefeMemo.setRootOn(true);
        setViewBuilders();
        setSettings();
        registerScreenOnOffReceiver();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        memoView.terminateMemo();
        realmController.realmClose();
        unregisterReceiver(screenOnOffReciever);
        stopForeground(true);
        preferences.unregisterOnSharedPreferenceChangeListener(listener);
        PefeMemo.setRootOn(false);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    //프리퍼런스 사용 세팅
    private SharedPreferences preferences;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    private void setPreferenceControl(){
        preferences = getSharedPreferences(PreferenceControlImpl.SAVE_SETTINGS,0);
        preferenceControl = new PreferenceControlImpl(preferences);
        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                setSettings();
            }
        };
        preferences.registerOnSharedPreferenceChangeListener(listener);
    }
    private void setControllers(){
        realmController = new RealmControllerImpl(this);
        realmController.realmInit();
        memoController = new MemoCotrollerImpl(realmController);
    }

    //View를 그리기위해 필요한 윈도우 매니저와 인플레이터를 MemoView클래스에 전달함
    private void setViewBuilders(){
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        memoView = new MemoViewImpl(this,memoController,wm);
        memoView.initMemo();
    }

    // 환경설정 반영하기
    private void setSettings(){
        memoUse = preferenceControl.restoreMemoUse();
        lockScreenUse = preferenceControl.restoreLockScreenUse();
        //Memo와 lockScreenUse의 변화마다 notification 사용 여부 변경
        onOffNotification();
    }


    private void registerScreenOnOffReceiver(){
        screenOnOffReciever = new ScreenOnOffReciever();
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(screenOnOffReciever, filter);
        filter = new IntentFilter(ACTION_SWITCH_MEMO);
        registerReceiver(screenOnOffReciever,filter);
        filter = new IntentFilter(ACTION_SWITCH_LOCKSCREEN);
        registerReceiver(screenOnOffReciever,filter);
    }
    //Notification 달기
    private static final int NOTI_ID = 3223;
    NotificationManager notiMng;
    private void onOffNotification(){
        notiMng = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        updateNoti();
//        if (noti == null) {
//            if(memoUse||lockScreenUse){
//                makeNotification();
//                notiMng.notify(NOTI_ID,noti);
//            }
//        }else{
//            if(memoUse||lockScreenUse){
//                makeNotification();
//                notiMng.notify(NOTI_ID,noti);
//            }
//            else{
//                notiMng.cancel(NOTI_ID);
//                noti = null;
//            }
//        }

    }
    private void updateNoti(){
        makeNotification();
        notiMng.notify(NOTI_ID,noti);
    }
    private final int NOTI_REQUEST = 8080;

    private void makeNotification(){
        // 노티 클릭시 앱으로 가기 위한 인텐트
        Intent intent = new Intent(this,MainViewImpl.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,NOTI_REQUEST,intent,0);
        Bitmap largeIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.pefelogo);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N){
            Notification.Builder builder = new Notification.Builder(this).setContentTitle("Click to go Pefe App")
                    .setSmallIcon(R.drawable.pefenoti)
                    .setLargeIcon(largeIcon)
                    .setOngoing(true)
                    .setContentIntent(pendingIntent)
                    .setContent(createNotficaitonView());
            noti = builder.build();
        }else{
            Notification.Builder builder = new Notification.Builder(this).setContentTitle("Click to go Pefe App")
                    .setSmallIcon(R.drawable.pefenoti)
                    .setLargeIcon(largeIcon)
                    .setOngoing(true)
                    .setContentIntent(pendingIntent)
                    .setCustomContentView(createNotficaitonView());
            noti =  builder.build();
        }
    }

    private RemoteViews createNotficaitonView(){
        Intent mIntent = new Intent();
        mIntent.setAction(ACTION_SWITCH_MEMO);
        PendingIntent pMIntent = PendingIntent.getBroadcast(this,0,mIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        Intent lIntent = new Intent(this,MainViewImpl.class);
        PendingIntent pLIntent = PendingIntent.getActivity(this,NOTI_REQUEST,lIntent,0);
        RemoteViews result = new RemoteViews(getPackageName(),R.layout.noti_onoff);
        result.setOnClickPendingIntent(R.id.notiLogo,pLIntent);
        if(memoUse) {
            result.setImageViewResource(R.id.notiMemoOnOff, R.drawable.ic_event_available_white_36dp);
        }else{
            result.setImageViewResource(R.id.notiMemoOnOff, R.drawable.ic_event_busy_white_36dp);
        }
        result.setOnClickPendingIntent(R.id.notiMemoOnOff,pMIntent);;
        //result.setTextViewText(R.id.notiLockscreenOnOff,lockScreenUse+"");
        //result.setOnClickPendingIntent(R.id.notiLockscreenOnOff,pLIntent);

        return result;
    }




    private TelephonyManager telephonyManager = null;

    private static final String ACTION_SWITCH_MEMO ="Switch_MEMO";
    private static final String ACTION_SWITCH_LOCKSCREEN = "Switch_Lockscreen";

    private class ScreenOnOffReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch(action){
//                case Intent.ACTION_SCREEN_OFF:
//                    if (telephonyManager == null) {
//                        telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
//                        telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
//                    }
//                    if (lockScreenUse) {
//                        if (!phoneCallStatus) {
//                            Log.e("ScreenOFF","true");
//                            LockScreenView lockScreenView = new LockScreenViewImpl(RootService.this);
//                            lockScreenView.showLockScreen();
//                        }
//                    }
//                    break;

                case ACTION_SWITCH_MEMO:
                    memoUse = !memoUse;
                    preferenceControl.saveMemoUse(memoUse);
//                    updateNoti();
                    break;
//                case ACTION_SWITCH_LOCKSCREEN:
//                    lockScreenUse = !lockScreenUse;
//                    preferenceControl.saveMemoUse(lockScreenUse);
//                    updateNoti();
//                    break;
            }
        }

    }
//    private boolean phoneCallStatus = false;
//    private PhoneStateListener phoneListener = new PhoneStateListener() {
//
//        @Override
//        public void onCallStateChanged(int state, String incomingNumber) {
//            switch (state) {
//                //전화 올 때
//                case TelephonyManager.CALL_STATE_RINGING:
//                    phoneCallStatus = true;
//                    break;
//                //전화 받음
//                case TelephonyManager.CALL_STATE_OFFHOOK:
//                    phoneCallStatus = true;
//                    break;
//                //전화 끊음
//                case TelephonyManager.CALL_STATE_IDLE:
//                    phoneCallStatus = false;
//                    break;
//            }
//        }
//    };


}
