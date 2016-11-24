package com.pefe.pefememo;

import android.app.Application;

import com.pefe.pefememo.Realm.RealmControl;
import com.pefe.pefememo.Realm.RealmControlImpl;


public class PefeMemo extends Application {
    /*
     * Realm 설정하기
     */
    RealmControl realmControl = null;

    /*
    * RootService의 상태를 나타낸다.
    * 이를통해 앱이 켜졌을 경우 RootService를 실행 후 바인드를 할지, 바로 바인드를 실행할지 결정한다.
    */
    private static boolean isRootOn = false;

    public static boolean isRootOn() {return isRootOn;}
    public static void setIsRootOn(boolean isRootOn) {PefeMemo.isRootOn = isRootOn;}

    @Override
    public void onCreate() {
        super.onCreate();
        realmControl = new RealmControlImpl(this);
        realmControl.realmInit();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
