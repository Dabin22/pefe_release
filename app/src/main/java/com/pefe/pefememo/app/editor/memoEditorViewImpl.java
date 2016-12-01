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
import android.widget.Button;
import android.widget.EditText;
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

public class MemoEditorViewImpl extends AppCompatActivity implements MemoEditorView {
    RealmController realmController;

    Memo memo;

    ToggleButton importance;
    Button close,save,copy,folderImg;
    TextView folderName;
    EditText content;

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
        content = (EditText) findViewById(R.id.memoEditorContent);
        content.setText(memo.getContent());
        close = (Button) findViewById(R.id.memoEditorClose);
        save = (Button) findViewById(R.id.memoEditorSave);
        copy = (Button) findViewById(R.id.memoEditorCopy);
        folderImg =(Button)findViewById(R.id.memoEditorDirImg);
        folderName =(TextView)findViewById(R.id.memoEditorDirText);
        close.setOnClickListener(new CloseClickListener());
        save.setOnClickListener(new SaveClickListener());
        copy.setOnClickListener(new CopyClickListener(content));
        folderImg.setOnClickListener(new FolderSetListener(folderName));
        folderName.setOnClickListener(new FolderSetListener(folderName));
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
            String dirName = folderName.getText().toString();
            String dirCode = realmController.readADir(dirName).getCode();
            realmController.modifyMemo(memo.getNo(),important,dirCode,memoContent);
            Toast.makeText(MemoEditorViewImpl.this, "Memo Saved", Toast.LENGTH_SHORT).show();
            view.setVisibility(View.GONE);
        }
    }
    private class CloseClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            finish();

        }
    }

    private class FolderSetListener implements View.OnClickListener{
        TextView folderName = null;
        int who;
        OrderedRealmCollection<Directory> dirs;

        public FolderSetListener(TextView folderName) {
            this.folderName = folderName;
            dirs = realmController.readDirAll();
        }

        @Override
        public void onClick(View view) {
            AlertDialog dirSetDialog = setDirCreateDialog();
            dirSetDialog.show();
        }
        private void setTextOnName(){
            if(who != -1){
                folderName.setText(dirs.get(who).getName());
            }
        }

        private AlertDialog setDirCreateDialog() {
            String[] fList = new String[dirs.size()];
            for(int i = 0; i < fList.length; i++){
                fList[i] = dirs.get(i).getName();
            }
            AlertDialog dialog = new AlertDialog.Builder(MemoEditorViewImpl.this)
                    .setTitle("Set Folder")
                    .setSingleChoiceItems(fList, -1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            who = i;
                        }
                    })
                    .setPositiveButton("Confirm", new DirDialogCancelListener())
                    .setCancelable(true)
                    .create();
            return dialog;
        }
        private class DirDialogCancelListener implements DialogInterface.OnClickListener {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                setTextOnName();
            }
        }
    }
}
