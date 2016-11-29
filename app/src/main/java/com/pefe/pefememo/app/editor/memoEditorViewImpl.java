package com.pefe.pefememo.app.editor;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewTextChangeEvent;
import com.pefe.pefememo.R;
import com.pefe.pefememo.realm.RealmController;
import com.pefe.pefememo.realm.RealmControllerImpl;
import com.pefe.pefememo.app.fragments.memo.MemoViewAdapter;
import com.pefe.pefememo.model.memo.Memo;

import rx.Observable;
import rx.Subscriber;

public class MemoEditorViewImpl extends AppCompatActivity implements MemoEditorView {
    RealmController realmController;

    Memo memo;

    ToggleButton importance;
    Button close;
    Button save;
    Button copy;
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
        close.setOnClickListener(new CloseClickListener());
        save = (Button) findViewById(R.id.memoEditorSave);
        save.setOnClickListener(new SaveClickListener(content));
        copy = (Button) findViewById(R.id.memoEditorCopy);
        copy.setOnClickListener(new CopyClickListener(content));
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



    private class CopyClickListener implements View.OnClickListener{
        EditText content = null;
        String memoContent = null;

        public CopyClickListener(EditText content) {
            this.content = content;
            this.memoContent = content.getText().toString();
        }

        @Override
        public void onClick(View view) {
            copyMemo(memoContent);
        }
    }
    //출저 http://iw90.tistory.com/154
    private final String COPYCLIP_LABEL = "COPIED_MEMO";
    private void copyMemo(String content ){
        ClipboardManager clipboardManager = (ClipboardManager)this.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText(COPYCLIP_LABEL, content);
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(this,"Memo Copied", Toast.LENGTH_SHORT).show();
    }

    private class SaveClickListener implements View.OnClickListener{
        EditText content;

        private SaveClickListener(EditText content) {
            this.content = content;
        }
        @Override
        public void onClick(View view) {
            String memoContent = content.getText().toString();
            realmController.modifyMemo(memo.getNo(),memo.isImportant(),memo.getDirCode(),memoContent);
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
}
