package com.pefe.pefememo.Realm;

import android.content.Context;

import com.pefe.pefememo.model.directory.Directory;
import com.pefe.pefememo.model.memo.Memo;
import com.pefe.pefememo.model.todo.Todo;

import io.realm.DynamicRealm;
import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmAsyncTask;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmResults;
import io.realm.annotations.RealmModule;

//TODO REALM주석 달기

public class RealmControlImpl implements RealmControl {

    Context context;

    public static final String NAME_MEMO = "MEMO";
    private final int NEWEST_VERSION = 0;
    private static RealmConfiguration memoConfig;
    private Realm realm;

    public RealmControlImpl(Context context){
        this.context =context;
        realmConfig();
    }

    @Override
    public void realmInit() {
        realm = Realm.getInstance(memoConfig);
    }
    @Override
    public void realmClose(){
        realm.close();
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

//    @Override
//    public void addDir(long no, long order, String code, String name, String pw){
//        Realm memoRealm = Realm.getInstance(RealmControlImpl.memoConfig);
//        RealmAsyncTask addDirTask = memoRealm.executeTransactionAsync(new DirCreateTransaction(no,order,code,name,pw)
//                            ,new OnDirCreateSuccess()
//                            ,new OnDirCreateError());
//        addDirTask.cancel();
//        memoRealm.close();
//    }

    @Override
    public void addDir(long no, long order, String code, String name, String pw){
        Realm memoRealm = Realm.getInstance(RealmControlImpl.memoConfig);
        memoRealm.executeTransaction(new DirCreateTransaction(no,order,code,name,pw));
        memoRealm.close();
    }

    @Override
    public void modifyDir(String code,long order, String name, String pw){
        Realm memoRealm = Realm.getInstance(RealmControlImpl.memoConfig);
        RealmAsyncTask modifyDirTask = memoRealm.executeTransactionAsync(new DirModifyTransaction(order,code,name,pw)
                ,new OnDirModifySuccess()
                ,new OnDirModifyError());
        modifyDirTask.cancel();
        memoRealm.close();
    }

    @Override
    public void deleteDir(String code){
        Realm memoRealm = Realm.getInstance(RealmControlImpl.memoConfig);
        RealmAsyncTask deleteDirTask = memoRealm.executeTransactionAsync(new DirDeleteTransaction(code)
        ,new OnDirDeleteSuccess()
                ,new OnDirDeleteError());
        deleteDirTask.cancel();
        memoRealm.close();
    }

//    @Override
//    public void writeMemo(long no, boolean importance, String dirCode, String content){
//        Realm memoRealm = Realm.getInstance(RealmControlImpl.memoConfig);
//        RealmAsyncTask writeTask = memoRealm.executeTransactionAsync(new MemoCreateTransaction(no, importance, dirCode, content)
//                        , new OnMemoCreateSuccess()
//                        , new OnMemoCreateError());
//        writeTask.cancel();
//        memoRealm.close();
//    }
    @Override
    public void writeMemo(long no, boolean importance, String dirCode, String content){
        Realm memoRealm = Realm.getInstance(RealmControlImpl.memoConfig);
        memoRealm.executeTransaction(new MemoCreateTransaction(no, importance, dirCode, content));
        memoRealm.close();
    }
    @Override
    public void modifyMemo(long no, boolean importance, String dirCode, String content){
        Realm memoRealm = Realm.getInstance(RealmControlImpl.memoConfig);
        RealmAsyncTask modifyTask = memoRealm
                .executeTransactionAsync(new MemoModifyTransaction(no, importance, dirCode, content)
                        , new OnMemoModifySuccess()
                        , new OnMemoModifyError());
        modifyTask.cancel();
        memoRealm.close();
    }
    @Override
    public void deleteMemo(long no){
        Realm memoRealm = Realm.getInstance(RealmControlImpl.memoConfig);
        RealmAsyncTask deleteTask = memoRealm
                .executeTransactionAsync(new MemoDeleteTransaction(no)
                        , new OnMemoDeleteSuccess()
                        , new OnMemoDeleteError());
        deleteTask.cancel();
        memoRealm.close();
    }

    public Memo readAMemoByNO(long no){
        Realm memoRealm = Realm.getInstance(memoConfig);
        Memo result = memoRealm.where(Memo.class).equalTo("no",no).findFirst();
        memoRealm.close();
        return result;
    }

    @Override
    public OrderedRealmCollection<Memo> readMemoByImportance(String dirCode) {
        RealmResults<Memo> result = realm.where(Memo.class).equalTo("dirCode",dirCode).equalTo("important",true).findAll();
        return result;
    }

    @Override
    public OrderedRealmCollection<Memo> readMemoByDirCode(String dirCode) {
        RealmResults<Memo> result = realm.where(Memo.class).equalTo("dirCode",dirCode).findAll();
        return result;
    }

    @Override
    public OrderedRealmCollection<Memo> readMemoByContent(String keyWord, String dirCode) {
        RealmResults<Memo> result = realm.where(Memo.class).equalTo("dirCode",dirCode).contains("content",keyWord).findAll();
        return result;
    }

    @Override
    public OrderedRealmCollection<Directory> readDirAll() {
        RealmResults<Directory> result = realm.where(Directory.class).findAll();
        return result;
    }
    private class DirCreateTransaction implements Realm.Transaction{
        long no, order;
        String code,name,pw;

        public DirCreateTransaction(long no, long order, String code, String name, String pw) {
            this.no = no;
            this.order = order;
            this.code = code;
            this.name = name;
            this.pw = pw;
        }

        @Override
        public void execute(Realm realm) {
            Directory dir = realm.createObject(Directory.class,code);
            dir.setNo(no);
            dir.setOrder(order);
            dir.setName(name);
            dir.setPw(pw);
        }
    }
    private class OnDirCreateSuccess implements Realm.Transaction.OnSuccess {

        @Override
        public void onSuccess() {
        }
    }

    private class OnDirCreateError implements Realm.Transaction.OnError {

        @Override
        public void onError(Throwable error) {
            error.printStackTrace();
        }
    }

    private class DirModifyTransaction implements Realm.Transaction{
        long order;
        String code,name,pw;

        public DirModifyTransaction(long order, String code, String name, String pw) {
            this.order = order;
            this.code = code;
            this.name = name;
            this.pw = pw;
        }

        @Override
        public void execute(Realm realm) {
            Directory dir = realm.where(Directory.class).equalTo("code",code).findFirst();
            dir.setOrder(order);
            dir.setName(name);
            dir.setPw(pw);
        }
    }
    private class OnDirModifySuccess implements Realm.Transaction.OnSuccess {

        @Override
        public void onSuccess() {
        }
    }

    private class OnDirModifyError implements Realm.Transaction.OnError {

        @Override
        public void onError(Throwable error) {
            error.printStackTrace();
        }
    }

    private class DirDeleteTransaction implements Realm.Transaction{

        String code;

        public DirDeleteTransaction(String code) {
            this.code = code;
        }

        @Override
        public void execute(Realm realm) {
            Directory dir = realm.where(Directory.class).equalTo("code",code).findFirst();
            dir.deleteFromRealm();
        }
    }
    private class OnDirDeleteSuccess implements Realm.Transaction.OnSuccess {

        @Override
        public void onSuccess() {
        }
    }

    private class OnDirDeleteError implements Realm.Transaction.OnError {

        @Override
        public void onError(Throwable error) {
            error.printStackTrace();
        }
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
            Memo newMemo = realm.createObject(Memo.class,no);
            newMemo.setImportant(importance);
            newMemo.setContent(content);
            newMemo.setDirCode(dirCode);
        }
    }


    private class OnMemoCreateSuccess implements Realm.Transaction.OnSuccess {

        @Override
        public void onSuccess() {
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
