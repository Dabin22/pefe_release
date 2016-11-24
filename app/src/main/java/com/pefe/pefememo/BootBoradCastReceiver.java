package com.pefe.pefememo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.pefe.pefememo.memo.rootService.RootService;

public class BootBoradCastReceiver extends BroadcastReceiver {
    public BootBoradCastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Intent service_intent_Sensor;
        if(action.equals(Intent.ACTION_BOOT_COMPLETED)){
            service_intent_Sensor = new Intent(context, RootService.class);
            context.startService(service_intent_Sensor);
        }
    }
}
