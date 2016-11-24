package com.pefe.pefememo.Realm;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.pefe.pefememo.R;
import com.pefe.pefememo.model.directory.Directory;
import com.pefe.pefememo.model.memo.Memo;
import com.pefe.pefememo.model.todo.Todo;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmAsyncTask;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.annotations.RealmModule;

//TODO REALM주석 달기

public class RealmControlImpl implements RealmControl {

    Application app;
    Context context;

    public static final String NAME_MEMO = "MEMO";

    private final int NEWEST_VERSION = 0;

    private static RealmConfiguration memoConfig;


    public RealmControlImpl(Application app){
        this.app = app;
    }
    public RealmControlImpl(Context context){
        this.context =context;
    }

    @Override
    public void realmInit() {
        Realm.init(app);
        realmConfig();
    }
    private void realmConfig(){
        memoConfig = new RealmConfiguration.Builder()
                .name(NAME_MEMO)
                //.encryptionKey()
                .schemaVersion(NEWEST_VERSION)
                .modules(new MemoModule())
                .deleteRealmIfMigrationNeeded()
                .build();
    }

    @RealmModule(classes={Memo.class, Directory.class, Todo.class})
    private class MemoModule{}

    @Override
    public void writeMemo(View innerMemo , long no, boolean importance, String dirCode, String content){
        Realm memoRealm = Realm.getInstance(RealmControlImpl.memoConfig);
        Log.e("Save","almost");
        memoRealm.executeTransactionAsync(new MemoCreateTransaction(no, importance, dirCode, content)
                        , new OnMemoCreateSuccess(innerMemo)
                        , new OnMemoCreateError());
        memoRealm.close();
    }
    @Override
    public void modifyMemo(long no, boolean importance, String dirCode, String content){
        Realm memoRealm = Realm.getInstance(RealmControlImpl.memoConfig);
        RealmAsyncTask regularTransactionAsync = memoRealm
                .executeTransactionAsync(new MemoModifyTransaction(no, importance, dirCode, content)
                        , new OnMemoModifySuccess()
                        , new OnMemoModifyError());
        regularTransactionAsync.cancel();
        memoRealm.close();
    }
    @Override
    public void deleteMemo(long no){
        Realm memoRealm = Realm.getInstance(RealmControlImpl.memoConfig);
        RealmAsyncTask regularTransactionAsync = memoRealm
                .executeTransactionAsync(new MemoDeleteTransaction(no)
                        , new OnMemoDeleteSuccess()
                        , new OnMemoDeleteError());
        regularTransactionAsync.cancel();
        memoRealm.close();
    }



    private class MemoCreateTransaction implements Realm.Transaction {
        long no;
        boolean importance;
        String dirCode;
        String content;

        private MemoCreateTransaction(long no, boolean importance, String dirCode , String content) {
            this.no = no;
            this.importance = importance;
            this.content = content;
            this.dirCode = dirCode;
        }

        @Override
        public void execute(Realm realm) {
            Memo newMemo = realm.createObject(Memo.class);
            newMemo.setNo(no);
            newMemo.setImportant(importance);
            newMemo.setContent(content);
            newMemo.setDirCode(dirCode);
        }
    }

    private class OnMemoCreateSuccess implements Realm.Transaction.OnSuccess {
        View innerMemo;
        OnMemoCreateSuccess(View innerMemo) {
            this.innerMemo = innerMemo;
        }
        @Override
        public void onSuccess() {
            ((EditText)innerMemo.findViewById(R.id.memoContent)).setText("");
        }
    }

    private class OnMemoCreateError implements Realm.Transaction.OnError {

        @Override
        public void onError(Throwable error) {
            error.printStackTrace();
        }
    }

    private class MemoModifyTransaction implements Realm.Transaction{

        long no;
        boolean importance;
        String dirCode;
        String content;

        private MemoModifyTransaction(long no, boolean importance, String dirCode, String content) {
            this.no = no;
            this.importance = importance;
            this.content = content;
            this.dirCode = dirCode;
        }

        @Override
        public void execute(Realm realm) {
            Memo modifyMemo = realm.where(Memo.class)
                    .equalTo("no", no)
                    .findFirst();
            modifyMemo.setImportant(importance);
            modifyMemo.setDirCode(dirCode);
            modifyMemo.setContent(content);
        }
    }
    private class OnMemoModifySuccess implements Realm.Transaction.OnSuccess{

        @Override
        public void onSuccess() {
            //directory 내 Board 새로고침
        }
    }
    private class OnMemoModifyError implements Realm.Transaction.OnError{

        @Override
        public void onError(Throwable error) {
            error.printStackTrace();
        }
    }

    private class MemoDeleteTransaction implements Realm.Transaction {
        Long no;
        private MemoDeleteTransaction(long no) {
            this.no = no;
        }

        @Override
        public void execute(Realm realm) {
            Memo deleteMemo = realm.where(Memo.class).equalTo("no",no).findFirst();
            deleteMemo.deleteFromRealm();
        }
    }

    private class OnMemoDeleteSuccess implements Realm.Transaction.OnSuccess {
        @Override
        public void onSuccess() {

        }
    }

    private class OnMemoDeleteError implements Realm.Transaction.OnError {
        @Override
        public void onError(Throwable error) {

        }
    }


    private class MemoMigration implements RealmMigration {
        @Override
        public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        }
    }

    private class TodoMemoMigration implements RealmMigration {
        @Override
        public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {}
    }

    private class DirectoryMigration implements RealmMigration {
        @Override
        public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {}
    }

    private class SchedularMigration implements RealmMigration {
        @Override
        public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {}
    }



}
