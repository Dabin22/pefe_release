package com.pefe.pefememo.memo.memo;

import android.content.Context;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.pefe.pefememo.R;
import com.pefe.pefememo.app.fragments.memo.DirViewAdapter;
import com.pefe.pefememo.app.fragments.memo.MemoFragment;
import com.pefe.pefememo.memo.rootservice.RootService;
import com.pefe.pefememo.model.directory.Directory;
import com.pefe.pefememo.model.memo.Memo;
import com.pefe.pefememo.sample.Sample;
import com.pefe.pefememo.tools.CopyTool;

import java.util.ArrayList;
import java.util.Date;

import io.realm.OrderedRealmCollection;
import io.realm.Sort;

import static android.content.Context.TELEPHONY_SERVICE;
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
    private TelephonyManager telephonyManager = null;
    //private LayoutInflater inflater = null;
    private float displayHeight;
    private float displayWidth;

    private int innerType = 0;
    private final int numbOfType = 2;
    private final int MEMO = 0;
    private final int TODO = 1;

    private View trnspBtn = null;

    private WindowManager.LayoutParams memoParams;
    private WindowFrame memoFrame;
    private View innerMemo = null;
    Spinner folderSpinner = null;
    private View innerTodo = null;

    private boolean phoneCallStatus = false;
    private boolean memoOnOff = false;

    public MemoViewImpl(Context context,MemoController memoController,WindowManager wm) {
        this.memoController = memoController;
        this.context = context;
        this.wm = wm;
    }

    @Override
    public void initMemo() {
        trnspBtn = View.inflate(context,R.layout.item_trnspbtn,null);
        trnspBtn.setAlpha(0.0f);
        trnspBtn.setOnTouchListener(new TrnspBtnListener());
        setTrnspBtn();
        setDefaultMemo();
        setTelephonyManager();

        memoParams = setMemoParams();
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
    private void setTrnspBtnImmediate(){
            wm.removeViewImmediate(memoFrame);
            wm.addView(trnspBtn, setTrnspBtnParams());
    }


    //wm에 메모 뷰를 넣는 역할, 투명버튼에서 메모로 전환도 담당
    private void setMemo(){
        if(!memoOnOff){
            wm.removeViewImmediate(trnspBtn);
            memoParams = setMemoParams();
            wm.addView(memoFrame,memoParams);
            setFolderSpinner(folderSpinner);
            memoOnOff= true;
        }else{
            if(memoBeforePhone){
                memoBeforePhone = false;
                memoParams = setMemoParams();
                wm.removeViewImmediate(trnspBtn);
                wm.addView(memoFrame,memoParams);
                setFolderSpinner(folderSpinner);
                memoOnOff= true;
            }
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
        displayHeight = context.getResources().getDisplayMetrics().heightPixels;
        displayWidth = context.getResources().getDisplayMetrics().widthPixels;
        float btnHeight = (int) displayHeight/2.5f;
        int btnWidth = (int) displayWidth/32;
        WindowManager.LayoutParams params= new WindowManager.LayoutParams(
                btnWidth,
                (int)btnHeight,
                WindowManager.LayoutParams.TYPE_PHONE, FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.BOTTOM | Gravity.END;
        return params;
    }

    private WindowManager.LayoutParams setMemoParams(){
        displayHeight = context.getResources().getDisplayMetrics().heightPixels;
        displayWidth = context.getResources().getDisplayMetrics().widthPixels;
        float memoHeight = (int) displayHeight/2.5f;
        WindowManager.LayoutParams memoParams  = new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT,
                (int) memoHeight,
                WindowManager.LayoutParams.TYPE_PHONE
                ,FLAG_NOT_TOUCH_MODAL|FLAG_SPLIT_TOUCH
                , PixelFormat.TRANSLUCENT);
        memoParams.y = (int)(displayHeight/2.5f);
        memoParams.windowAnimations = android.R.style.Animation_Translucent;
//        memoParams.gravity= Gravity.BOTTOM | Gravity.END;
        return memoParams;
    }
    //메모 내부 초기화

    private void setDefaultMemo(){
//        memoRoot = new FrameLayout(context);
        memoFrame = new WindowFrame(context);
        View memo = View.inflate(context,R.layout.window_memo,memoFrame);
        innerMemo = memo.findViewById(R.id.innerMemo);
        setDefaultInnerMemo(innerMemo);
        innerTodo = memo.findViewById(R.id.innerTodo);
        setDefaultInnerTodo(innerTodo);

        RelativeLayout memoBar = (RelativeLayout) memo.findViewById(R.id.memoBar);

        Button exitBtn = (Button) memo.findViewById(R.id.exitBtn);
        Button saveBtn = (Button) memo.findViewById(R.id.saveBtn);
        Button purposeBtn = (Button) memo.findViewById(R.id.purposeBtn);
        SeekBar trnspController = (SeekBar) memo.findViewById(R.id.trnspController);

        memoBar.setOnTouchListener(new BarTouchListener());
        exitBtn.setOnClickListener(new exitListener());
        saveBtn.setOnClickListener(new saveListener());
        purposeBtn.setOnClickListener(new purposeListener());
        //TODO MEMO값 스트링 값으로 넣어주기
        purposeBtn.setText("MEMO");
        trnspController.setOnSeekBarChangeListener(new TrnspChangeListener());
    }
    //메모 작성 뷰 초기화
    private void setDefaultInnerMemo(View innerMemo){
        defaultingInnerMemo(innerMemo);
    }
    private void defaultingInnerMemo(View innerMemo){
        LinearLayout innerMemoMenuBar = (LinearLayout) innerMemo.findViewById(R.id.innerMemoMenuBar);
        ToggleButton importanceTBtn = (ToggleButton)innerMemo.findViewById(R.id.memoImportance);
        EditText title = (EditText)innerMemo.findViewById(R.id.title);
        EditText content = (EditText)innerMemo.findViewById(R.id.content);
        content.setOnLongClickListener(new ContentLongClickListener(innerMemoMenuBar));
        Button cleanBtn = (Button) innerMemo.findViewById(R.id.cleanBtn);
        Button copyBtn = (Button) innerMemo.findViewById(R.id.copyBtn);
        Button pasteBtn = (Button) innerMemo.findViewById(R.id.pasteBtn);

//        innerMemoBar.setOnTouchListener(new BarTouchListener());
        cleanBtn.setOnClickListener(new onInnerMemoMenuClick(title,content));
        copyBtn.setOnClickListener(new onInnerMemoMenuClick(title,content));
        pasteBtn.setOnClickListener(new onInnerMemoMenuClick(title,content));
        folderSpinner = (Spinner)innerMemo.findViewById(R.id.dirSpinner);
        setFolderSpinner(folderSpinner);
    }
    private void setTelephonyManager(){
        if (telephonyManager == null) {
            telephonyManager = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
            telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }
    private class ContentLongClickListener implements  View.OnLongClickListener{
        View innerMemoMenuBar;

        public ContentLongClickListener(View innerMemoMenuBar) {
            this.innerMemoMenuBar = innerMemoMenuBar;
        }

        @Override
        public boolean onLongClick(View view) {
            innerMemoMenuBar.setVisibility(View.VISIBLE);
            Handler mHandler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    innerMemoMenuBar.setVisibility(View.GONE);
                }
            };

            mHandler.postDelayed(runnable,4000);

            return true;
        }
    }
    private class onInnerMemoMenuClick implements View.OnClickListener{
        EditText title,content;

        public onInnerMemoMenuClick(EditText title, EditText content) {
            this.title = title;
            this.content = content;
        }

        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.cleanBtn:
                    content.setText("");
                    title.setText("");
                    break;
                case R.id.copyBtn:
                    CopyTool.copyMemo(content);
                    break;
                case R.id.pasteBtn:
                    CopyTool.pasteText(content);
                    break;
            }
        }
    }
    OrderedRealmCollection<Directory> dirs;
    private void setFolderSpinner(Spinner spinner){
        dirs = memoController.getDirs().sort("order", Sort.ASCENDING);
        String[] fList = new String[(dirs.size()+1)];
        for(int i = 0; i < fList.length; i++){
            if(i == 0){
                fList[0] = "No Folder";
                continue;
            }
            fList[i] = dirs.get(i-1).getName();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,R.layout.item_spinner,fList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

    }
    //todo메모 작성 뷰 초기화화
    private ArrayList<View>todoList;
    private final float TEMP_TODOITEM_ALPHA =0.3f;
    private void setDefaultInnerTodo(View innerTodo){
        todoList = new ArrayList<>();
        GridLayout todoBoard = (GridLayout) innerTodo.findViewById(R.id.todoBoard);
        defaultingTodo(todoBoard);
    }
    private void defaultingTodo(GridLayout todoBoard){
        View todoItem = View.inflate(context,R.layout.item_todo,null);
        ToggleButton repeatOnceBtn = (ToggleButton) todoItem.findViewById(R.id.todoOnceRepeatBtn);
        ToggleButton addDeleteBtn = (ToggleButton)todoItem.findViewById(R.id.todoAddDelBtn);
        addDeleteBtn.setOnCheckedChangeListener(new DeleteAddTodoListener());
        EditText content = (EditText)todoItem.findViewById(R.id.todoContent);
        content.setOnClickListener(new AddTodoItemListener());
        todoItem.setAlpha(TEMP_TODOITEM_ALPHA);
        todoBoard.addView(todoItem);
    }
    //투명버튼 터치 리스너, 동작으로 메모를 띄울 지 설정한다

    private class TrnspBtnListener implements View.OnTouchListener{
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if(RootService.memoUse){
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        trnspBtn.setAlpha(0.7f);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float motionX = motionEvent.getX();
                        float motionRange = -(displayWidth/6);
                        if (motionX < motionRange) {
                            setMemo();
                            trnspBtn.setAlpha(0.0f);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        trnspBtn.setAlpha(0.0f);
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
                    ToggleButton importanceTBtn = (ToggleButton)innerMemo.findViewById(R.id.memoImportance);
                    EditText title = (EditText)innerMemo.findViewById(R.id.title);
                    EditText content = (EditText)innerMemo.findViewById(R.id.content);
                    Spinner folderSpinner = (Spinner)innerMemo.findViewById(R.id.dirSpinner);

                    boolean important = importanceTBtn.isChecked();
                    String dirCode = "";
                    int i = folderSpinner.getSelectedItemPosition();
                    if(i > 0){
                        dirCode = dirs.get(i-1).getCode();
                    }
                    String memoTitle = title.getText().toString();
                    String memoContent = content.getText().toString();
                    memoController.saveMemo(important,dirCode,memoTitle,memoContent);

//                    importanceTBtn.setChecked(false);
//                    memoContent.setText("");
                    break;
                case TODO:
                    for(View todoItem : todoList){
                        ToggleButton repeatBtn = (ToggleButton) todoItem.findViewById(R.id.todoOnceRepeatBtn);
                        EditText todoContent = (EditText) todoItem.findViewById(R.id.todoContent);
                        boolean repeat = repeatBtn.isChecked();
                        String tContent = todoContent.getText().toString();
                        if(tContent.isEmpty()){
                            continue;
                        }else{
                            memoController.saveTodo(repeat,tContent);
                        }
                    }
                    GridLayout todoBoard =(GridLayout)innerTodo.findViewById(R.id.todoBoard);
                    todoBoard.removeAllViews();
                    defaultingTodo(todoBoard);
                    todoList.clear();
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
            memoFrame.setAlpha(trnsp + 0.3f);
        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {}
    }
    private float mTouchX, mTouchY;
    private int mViewX, mViewY;
    private class BarTouchListener implements View.OnTouchListener{
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if(RootService.memoUse){

                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        mTouchX = motionEvent.getRawX();
                        mTouchY = motionEvent.getRawY();
                        mViewX = memoParams.x;
                        mViewY = memoParams.y;
                        memoParams.gravity = 0;
                        wm.updateViewLayout(memoFrame,memoParams);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int x = (int)(motionEvent.getRawX() - mTouchX);
                        int y = (int) (motionEvent.getRawY() - mTouchY);
                        memoParams.x = mViewX + x;
                        memoParams.y = mViewY + y;
                        wm.updateViewLayout(memoFrame,memoParams);

                        break;
                }
            }
            return true;
        }
    }


    private class DeleteAddTodoListener implements CompoundButton.OnCheckedChangeListener{
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            GridLayout todoBoard = (GridLayout) compoundButton.getParent().getParent().getParent().getParent();
            View todoItem = (View) compoundButton.getParent().getParent().getParent();
            if(!b){
                EditText content = (EditText) todoItem.findViewById(R.id.todoContent);
                content.setText("");
                todoList.remove(todoItem);
                todoBoard.removeView(todoItem);
                todoItem = null;
            }
            else{
                View parent =(View) compoundButton.getParent().getParent().getParent();
                parent.setAlpha(1f);
                ToggleButton repeatOnceBtn = (ToggleButton) parent.findViewById(R.id.todoOnceRepeatBtn);
                repeatOnceBtn.setClickable(true);
                todoList.add(parent);


                View newAddTodo = View.inflate(context,R.layout.item_todo,null);
                ToggleButton deleteAddBtn = (ToggleButton) newAddTodo.findViewById(R.id.todoAddDelBtn);
                EditText content = (EditText)newAddTodo.findViewById(R.id.todoContent);
                content.setOnClickListener(new AddTodoItemListener());
                deleteAddBtn.setOnCheckedChangeListener(new DeleteAddTodoListener());
                todoBoard.addView(newAddTodo);
                newAddTodo.setAlpha(0.3f);}
        }
    }
    private class AddTodoItemListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            View todoItem = (View) view.getParent().getParent().getParent();
            ToggleButton deleteAddButton = (ToggleButton) todoItem.findViewById(R.id.todoAddDelBtn);
            deleteAddButton.setChecked(true);
            EditText content = (EditText)view;
            content.setFocusable(true);
            content.setFocusableInTouchMode(true);
            todoItem.setAlpha(1f);
        }
    }
    private boolean memoBeforePhone = false;
    private PhoneStateListener phoneListener = new PhoneStateListener() {

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                //전화 올 때
                case TelephonyManager.CALL_STATE_RINGING:
                    phoneCallStatus = true;
                        if(memoOnOff){
                            memoBeforePhone = true;
                            setTrnspBtnImmediate();
                        }
                    break;
                //전화 받음
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    phoneCallStatus = true;
                    break;
                //전화 끊음
                case TelephonyManager.CALL_STATE_IDLE:
                    phoneCallStatus = false;
                    if(memoBeforePhone&&memoOnOff){
                        setMemo();
                    }
                    break;
            }
        }
    };

    private class WindowFrame extends FrameLayout {
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
                setTrnspBtn();
                return true;
            }
            return super.dispatchKeyEvent(event);
        }
    }
}

