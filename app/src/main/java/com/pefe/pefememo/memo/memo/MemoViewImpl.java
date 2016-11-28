package com.pefe.pefememo.memo.memo;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.ToggleButton;

import com.pefe.pefememo.R;
import com.pefe.pefememo.memo.rootservice.RootService;

import java.util.ArrayList;

import static android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
import static android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
import static android.view.WindowManager.LayoutParams.FLAG_SECURE;
import static android.view.WindowManager.LayoutParams.FLAG_SPLIT_TOUCH;

/**
 * Created by dodoproject on 2016-11-24.
 */

public class MemoViewImpl implements MemoView {

    private MemoController memoController;

    private Context context = null;
    private WindowManager wm = null;
    private LayoutInflater inflater = null;
    private float displayHeight;
    private float displayWidth;

    private int innerType = 0;
    private final int numbOfType = 2;
    private final int MEMO = 0;
    private final int TODO = 1;

    private View trnspBtn = null;

//    private FrameLayout memoRoot = null;
    View memoFrame;
    private View innerMemo = null;
    private View innerTodo = null;

    private boolean memoOnOff = false;

    public MemoViewImpl(Context context, WindowManager wm, LayoutInflater inflater, float displayHeight, float displayWidth) {
        this.memoController = new MemoCotrollerImpl();
        this.context = context;
        this.wm = wm;
        this.inflater = inflater;
        this.displayHeight = displayHeight;
        this.displayWidth = displayWidth;
    }

    @Override
    public void initMemo() {
        trnspBtn = new View(context);
        trnspBtn.setOnTouchListener(new TrnspBtnListener());
        setTrnspBtn();

        setDefaultMemo();
    }

    @Override
    public void terminateMemo() {
        removeWm();
    }

    //동작 감지를 위한 투명버튼 세팅, 메모에서 버튼으로 윈도우 매니저에 넣어진 뷰를 변화시킬 수 있음
    private void setTrnspBtn(){
        //메모가 켜져있을 경우 root제거
        Log.e("memoStatus",memoOnOff+"");
        if(memoOnOff) {
            wm.removeViewImmediate(memoFrame);
            wm.addView(trnspBtn, setTrnspBtnParams());
            memoOnOff = false;
        }else{
            wm.addView(trnspBtn, setTrnspBtnParams());
        }
    }


    //wm에 메모 뷰를 넣는 역할, 투명버튼에서 메모로 전환도 담당
    private void setMemo(){
        if(!memoOnOff){
            wm.removeViewImmediate(trnspBtn);
            wm.addView(memoFrame,setMemoParams());
            memoOnOff= true;
        }
    }

    //wm안의 뷰를 모두 없애고 wm도 초기화
    private void removeWm(){
        if(memoOnOff){
            wm.removeViewImmediate(memoFrame);
        }else if(trnspBtn != null){
            wm.removeViewImmediate(trnspBtn);
        }
        memoFrame = null;
        trnspBtn = null;
        wm = null;
    }

    //투명버튼 파라미터 설정 값 세팅
    private WindowManager.LayoutParams setTrnspBtnParams(){
        int btnHeight = (int) displayHeight/3;
        int btnWidth = (int) displayWidth/40;
        WindowManager.LayoutParams params= new WindowManager.LayoutParams(
                btnWidth,
                btnHeight,
                WindowManager.LayoutParams.TYPE_PHONE, FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.BOTTOM | Gravity.END;
        return params;
    }

    private WindowManager.LayoutParams setMemoParams(){
        //TODO 사용자에게 정의된 비율 사용
        int memoHeight = (int) displayHeight/2;
        WindowManager.LayoutParams memoParams  = new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT,
                memoHeight,
                WindowManager.LayoutParams.TYPE_PHONE
                ,FLAG_NOT_TOUCH_MODAL|FLAG_SPLIT_TOUCH|FLAG_SECURE
                , PixelFormat.TRANSLUCENT);
        memoParams.gravity= Gravity.BOTTOM | Gravity.END;
        return memoParams;
    }
    //메모 내부 초기화
    private void setDefaultMemo(){
//        memoRoot = new FrameLayout(context);
        memoFrame = View.inflate(context,R.layout.window_memo,null);
        innerMemo = memoFrame.findViewById(R.id.innerMemo);
        setDefaultInnerMemo(innerMemo);
        innerTodo = memoFrame.findViewById(R.id.innerTodo);
        setDefaultInnerTodo(innerTodo);

        Button exitBtn = (Button) memoFrame.findViewById(R.id.exitBtn);
        Button saveBtn = (Button) memoFrame.findViewById(R.id.saveBtn);
        Button purposeBtn = (Button) memoFrame.findViewById(R.id.purposeBtn);
        SeekBar trnspController = (SeekBar) memoFrame.findViewById(R.id.trnspController);

        exitBtn.setOnClickListener(new exitListener());
        saveBtn.setOnClickListener(new saveListener());
        purposeBtn.setOnClickListener(new purposeListener());
        trnspController.setOnSeekBarChangeListener(new TrnspChangeListener());
    }
    //메모 작성 뷰 초기화
    private void setDefaultInnerMemo(View innerMemo){
        RadioGroup colorParllet;
        ToggleButton importanceTBtn = (ToggleButton)innerMemo.findViewById(R.id.memoImportance);
        Spinner dirSpinner = (Spinner)innerMemo.findViewById(R.id.dirSpinner);
        EditText memoContent = (EditText)innerMemo.findViewById(R.id.memoContent);
        //TODO 각 요소 상태 초기화(필요할 경우)
    }
    //todo메모 작성 뷰 초기화화
    private ArrayList<View>todoList;
    private final float TEMP_TODOITEM_ALPHA =0.3f;
    private void setDefaultInnerTodo(View innerTodo){
        todoList = new ArrayList<>();
        GridLayout todoBoard = (GridLayout) innerTodo.findViewById(R.id.todoBoard);
        View todoItem = inflater.inflate(R.layout.item_todo,todoBoard);
        todoItem.setAlpha(TEMP_TODOITEM_ALPHA);
        Button addDeleteBtn = (Button)todoItem.findViewById(R.id.todoAddDelBtn);
        addDeleteBtn.setOnClickListener(new AddDelListener());

    }
    //투명버튼 터치 리스너, 동작으로 메모를 띄울 지 설정한다

    private class TrnspBtnListener implements View.OnTouchListener{
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if(RootService.memoUse){
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float motionX = motionEvent.getX();
                        float motionRange = -(displayWidth/3);
                        if (motionX < motionRange) {
                            setMemo();
                        }
                        break;
                }
            }
            return true;
        }
    }
    private class exitListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            setTrnspBtn();
        }
    }

    private class saveListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            switch (innerType){
                case MEMO:
                    break;
                case TODO:
                    break;
            }

        }
    }

    private class purposeListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            innerType = (innerType+1)%numbOfType;
            switch (innerType){
                case MEMO:
                    ((Button)view).setText(R.string.memo);
                    innerMemo.setVisibility(View.VISIBLE);
                    innerTodo.setVisibility(View.GONE);
                    break;
                case TODO:
                    ((Button)view).setText(R.string.todo);
                    innerTodo.setVisibility(View.VISIBLE);
                    innerMemo.setVisibility(View.GONE);
                    break;
            }
        }
    }
    private class TrnspChangeListener implements SeekBar.OnSeekBarChangeListener{
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            float trnsp = i/1000f;
            memoFrame.setAlpha(trnsp);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {}
    }
    private class AddDelListener implements View.OnClickListener{
        boolean addDel = false;
        @Override
        public void onClick(View view) {
            addDel = !addDel;
            GridLayout todoBoard = (GridLayout) view.getParent().getParent();
            View todoItem = (View) view.getParent();
            if(addDel){
                //TODO 회전 ObjectAnimator
                //todoitem 활성화
                todoItem.setAlpha(1f);
                todoList.add(todoItem);
                //새 todoitem 넣기
                View newTodoItem = inflater.inflate(R.layout.item_todo,todoBoard);
                todoItem.setAlpha(TEMP_TODOITEM_ALPHA);
                Button addDeleteBtn = (Button)todoItem.findViewById(R.id.todoAddDelBtn);
                addDeleteBtn.setOnClickListener(new AddDelListener());

            }else{
                todoItem.setAlpha(TEMP_TODOITEM_ALPHA);
                todoBoard.removeView(todoItem);
                todoItem = null;
                todoBoard = null;
            }
        }
    }

}
