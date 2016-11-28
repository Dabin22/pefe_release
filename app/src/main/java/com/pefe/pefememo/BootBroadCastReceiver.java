package com.pefe.pefememo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.pefe.pefememo.memo.rootservice.RootService;

public class BootBroadCastReceiver extends BroadcastReceiver {
    public BootBroadCastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Intent serviceStartIntent;
        if(action.equals(Intent.ACTION_BOOT_COMPLETED)){
            serviceStartIntent = new Intent(context, RootService.class);
            context.startService(serviceStartIntent);
        }
    }
}
