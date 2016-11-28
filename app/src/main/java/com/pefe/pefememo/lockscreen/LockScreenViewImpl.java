package com.pefe.pefememo.lockscreen;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.pefe.pefememo.R;

public class LockScreenViewImpl extends AppCompatActivity implements LockScreenView{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_screen_view);
    }
}
