package com.pefe.pefememo;

import android.app.Application;
import io.realm.Realm;


public class PefeMemo extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    /*
    * RootService의 상태를 나타낸다.
    * 이를통해 앱이 켜졌을 경우 RootService를 실행 후 바인드를 할지, 바로 바인드를 실행할지 결정한다.
    */
    private static boolean isRootOn = false;
    public static boolean isRootOn() {return isRootOn;}
    public static void setRootOn(boolean isRootOn) {PefeMemo.isRootOn = isRootOn;}

}
