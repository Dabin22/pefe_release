package com.pefe.pefememo.memo.memo;

import android.content.Context;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.graphics.PixelFormat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RadioGroup;
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
    //private LayoutInflater inflater = null;
    private float displayHeight;
    private float displayWidth;

    private int innerType = 0;
    private final int numbOfType = 2;
    private final int MEMO = 0;
    private final int TODO = 1;

    private View trnspBtn = null;

//    private FrameLayout memoRoot = null;
    private View memoFrame;
    private View innerMemo = null;
    private View innerTodo = null;

    private boolean memoOnOff = false;

    public MemoViewImpl(Context context,MemoController memoController,WindowManager wm, float displayHeight, float displayWidth) {
        this.memoController = memoController;
        this.context = context;
        this.wm = wm;
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
        //TODO MEMO값 스트링 값으로 넣어주기
        purposeBtn.setText("MEMO");
        trnspController.setOnSeekBarChangeListener(new TrnspChangeListener());
    }
    //메모 작성 뷰 초기화
    private void setDefaultInnerMemo(View innerMemo){
        defaultingMemo(innerMemo);
    }
    private void defaultingMemo(View innerMemo){
        ToggleButton importanceTBtn = (ToggleButton)innerMemo.findViewById(R.id.memoImportance);
        EditText memoContent = (EditText)innerMemo.findViewById(R.id.memoContent);
        Button cleanBtn = (Button) innerMemo.findViewById(R.id.cleanBtn);
        Button copyBtn = (Button) innerMemo.findViewById(R.id.copyBtn);
        Button pasteBtn = (Button) innerMemo.findViewById(R.id.pasteBtn);
        Button selectAllBtn = (Button) innerMemo.findViewById(R.id.selectAllBtn);
        cleanBtn.setOnClickListener(new onInnerMemoMenuClick(memoContent));
        copyBtn.setOnClickListener(new onInnerMemoMenuClick(memoContent));
        pasteBtn.setOnClickListener(new onInnerMemoMenuClick(memoContent));
        selectAllBtn.setOnClickListener(new onInnerMemoMenuClick(memoContent));
        Spinner folderSpinner = (Spinner)innerMemo.findViewById(R.id.dirSpinner);
        setFolderSpinner(folderSpinner);
        folderSpinner.setSelection(-1);
    }
    private class onInnerMemoMenuClick implements View.OnClickListener{
        EditText content;

        public onInnerMemoMenuClick(EditText content) {
            this.content = content;
        }

        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.cleanBtn:
                    content.setText("");
                    break;
                case R.id.copyBtn:
                    CopyTool.copyMemo(content);
                    break;
                case R.id.pasteBtn:
                    CopyTool.pasteText(content);
                    break;
                case R.id.selectAllBtn:
                    content.selectAll();
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
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,android.R.layout.simple_spinner_dropdown_item,fList);
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
                        view.setBackgroundColor(0x111111) ;

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
                    ToggleButton importanceTBtn = (ToggleButton)innerMemo.findViewById(R.id.memoImportance);
                    EditText memoContent = (EditText)innerMemo.findViewById(R.id.memoContent);
                    Spinner folderSpinner = (Spinner)innerMemo.findViewById(R.id.dirSpinner);

                    boolean important = importanceTBtn.isChecked();
                    String dirCode = "";
                    int i = folderSpinner.getSelectedItemPosition();
                    if(i > 0){
                        dirCode = dirs.get(i-1).getCode();
                    }
                    String content = memoContent.getText().toString();
                    memoController.saveMemo(important,dirCode,content);

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
            memoFrame.setAlpha(trnsp);
        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {}
    }

//    private class FolderSetListener implements View.OnClickListener{
//        TextView folderName = null;
//        int who;
//        OrderedRealmCollection<Directory> dirs;
//
//        public FolderSetListener(TextView folderName) {
//            this.folderName = folderName;
//            dirs = memoController.getDirs();
//        }
//
//        @Override
//        public void onClick(View view) {
//            AlertDialog dirSetDialog = setDirCreateDialog();
//            dirSetDialog.show();
//
//        }
//        private void setTextOnName(){
//            if(who != -1){
//                folderName.setText(dirs.get(who).getName());
//            }
//        }
//
//        private AlertDialog setDirCreateDialog() {
//            ArrayList<String> folders = new ArrayList<>();
//            for(Directory d : dirs){
//                folders.add(d.getName());
//            }
//            String[] fList = new String[folders.size()];
//            fList = folders.toArray(fList);
//            AlertDialog dialog = new AlertDialog.Builder(context)
//                    .setTitle("Set Folder")
//                    .setSingleChoiceItems(fList, -1, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            who = i;
//                        }
//                    })
//                    .setNegativeButton("Confirm", new DirDialogCancelListener())
//                    .setCancelable(true)
//                    .create();
//            return dialog;
//        }
//        private class DirDialogCancelListener implements DialogInterface.OnClickListener {
//
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                dialogInterface.dismiss();
//            }
//        }
//    }


    private class DeleteAddTodoListener implements CompoundButton.OnCheckedChangeListener{
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            GridLayout todoBoard = (GridLayout) compoundButton.getParent().getParent();
            View todoItem = (View) compoundButton.getParent();
            if(!b){
                EditText content = (EditText) todoItem.findViewById(R.id.todoContent);
                content.setText("");
                todoList.remove(todoItem);
                todoBoard.removeView(todoItem);
                todoItem = null;
            }
            else{
                View parent =(View) compoundButton.getParent();
                parent.setAlpha(1f);
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
            View todoItem = (View) view.getParent();
            ToggleButton deleteAddButton = (ToggleButton) todoItem.findViewById(R.id.todoAddDelBtn);
            deleteAddButton.setChecked(true);
            EditText content = (EditText)view;
            content.setFocusable(true);
            content.setFocusableInTouchMode(true);
            todoItem.setAlpha(1f);
        }
    }
}

