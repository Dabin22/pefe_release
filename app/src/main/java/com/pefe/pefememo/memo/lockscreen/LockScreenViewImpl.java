package com.pefe.pefememo.memo.lockscreen;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.pefe.pefememo.R;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
import static android.view.WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;

/**
 * Created by dodoproject on 2016-12-03.
 */

public class LockScreenViewImpl implements LockScreenView {
    Context context;
    WindowManager wm ;

    public LockScreenViewImpl(Context context) {
        this.context = context;
        wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    @Override
    public void showLockScreen() {
        Log.e("LockScreen","almost Show");
        WindowFrame wf = new WindowFrame(context);
        View.inflate(context, R.layout.window_lock_screen,wf);
        wm.addView(wf,createParams());
    }
    private WindowManager.LayoutParams createParams(){
        WindowManager.LayoutParams params  = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT
                ,MATCH_PARENT
                ,WindowManager.LayoutParams.TYPE_PHONE
                ,FLAG_NOT_TOUCH_MODAL | FLAG_SHOW_WHEN_LOCKED
                , PixelFormat.TRANSLUCENT);
        return params;
    }
    public class WindowFrame extends FrameLayout {
        public WindowFrame(Context context) {
            super(context);
        }

        public WindowFrame(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public WindowFrame(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        @Override
        public boolean dispatchKeyEvent(KeyEvent event) {
            if(event.getKeyCode() == KeyEvent.KEYCODE_BACK){
                wm.removeViewImmediate(this);
                wm = null;
                context = null;
                return true;
            }
            return super.dispatchKeyEvent(event);
        }
    }
}
