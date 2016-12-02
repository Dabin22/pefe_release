package com.pefe.pefememo.app.editor;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.pefe.pefememo.R;
import com.pefe.pefememo.app.fragments.memo.MemoViewAdapter;
import com.pefe.pefememo.model.directory.Directory;
import com.pefe.pefememo.model.memo.Memo;
import com.pefe.pefememo.realm.RealmController;
import com.pefe.pefememo.realm.RealmControllerImpl;
import com.pefe.pefememo.tools.CopyTool;

import java.util.ArrayList;

import io.realm.OrderedRealmCollection;
import io.realm.Sort;

public class MemoEditorViewImpl extends AppCompatActivity implements MemoEditorView {
    RealmController realmController;

    Memo memo;

    ToggleButton importance;
    Button close,save,copy;
    Spinner folderName;
    EditText title,content;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_editor_view);
        realmController = new RealmControllerImpl(this);
        realmController.realmInit();

        long no = getIntent().getExtras().getLong(MemoViewAdapter.MEMO_NO);
        memo = realmController.readAMemoByNO(no);

        setMemo(memo);
    }
    private void setMemo(Memo memo){
        importance = (ToggleButton) findViewById(R.id.memoEditorImportance);
        importance.setChecked(memo.isImportant());
        title  = (EditText) findViewById(R.id.memoEditorTitle);
        title.setText(memo.getTitle());
        content = (EditText) findViewById(R.id.memoEditorContent);
        content.setText(memo.getContent());
        close = (Button) findViewById(R.id.memoEditorClose);
        save = (Button) findViewById(R.id.memoEditorSave);
        copy = (Button) findViewById(R.id.memoEditorCopy);
        folderName =(Spinner)findViewById(R.id.memoEditorDirSpinner);
        close.setOnClickListener(new CloseClickListener());
        save.setOnClickListener(new SaveClickListener());
        copy.setOnClickListener(new CopyClickListener(content));
        setFolderSpinner(folderName);
    }

    @Override
    protected void onStop() {
        super.onStop();
        realmController.taskClose();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realmController.realmClose();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        switch (action){
            case KeyEvent.KEYCODE_BACK:
                finish();
                break;
        }
        return super.dispatchKeyEvent(event);
    }

    private class CopyClickListener implements View.OnClickListener{
        EditText content = null;
        String memoContent = null;

        public CopyClickListener(EditText content) {
            this.content = content;
            this.memoContent = content.getText().toString();
        }

        @Override
        public void onClick(View view) {
            CopyTool.copyMemo(content);
        }
    }

    private class SaveClickListener implements View.OnClickListener{

        private SaveClickListener() {}
        @Override
        public void onClick(View view) {
            String memoContent = content.getText().toString();
            boolean important = importance.isChecked();
            String dirCode = "";
            int i = folderName.getSelectedItemPosition();
            if(i > 0){
                dirCode = dirs.get(i-1).getCode();
            }
            String memoTitle= "";
            memoTitle = title.getText().toString();
            realmController.modifyMemo(memo.getNo(),important,dirCode,memoTitle,memoContent);
            Toast.makeText(MemoEditorViewImpl.this, "Memo Saved", Toast.LENGTH_SHORT).show();
        }
    }
    private class CloseClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            finish();

        }
    }

    OrderedRealmCollection<Directory> dirs;
    private void setFolderSpinner(Spinner spinner){
        dirs = realmController.readDirAll().sort("order", Sort.ASCENDING);
        String[] fList = new String[(dirs.size()+1)];
        for(int i = 0; i < fList.length; i++){
            if(i == 0){
                fList[0] = "No Folder";
                continue;
            }
            fList[i] = dirs.get(i-1).getName();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,R.layout.item_spinner,fList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
 }
