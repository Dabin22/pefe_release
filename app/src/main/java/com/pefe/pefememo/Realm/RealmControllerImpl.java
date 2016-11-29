package com.pefe.pefememo.realm;

import android.content.Context;
import android.widget.Toast;

import com.pefe.pefememo.app.fragments.memo.MemoFragmentController;
import com.pefe.pefememo.model.directory.Directory;
import com.pefe.pefememo.model.memo.Memo;
import com.pefe.pefememo.model.todo.SelectedTodo;
import com.pefe.pefememo.model.todo.Todo;
import com.pefe.pefememo.sample.Sample;

import java.util.ArrayList;
import java.util.Date;

import io.realm.DynamicRealm;
import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmAsyncTask;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmResults;
import io.realm.annotations.RealmModule;

//TODO REALM주석 달기

public class RealmControllerImpl implements RealmController {

    Context context;

    public static final String DIR = "DIR";
    public static final String MEMO = "MEMO";
    public static final String TODO = "TODO";
    public static final String SELECTED_TODO = "SELECTED_TODO";


    private final int NEWEST_VERSION = 0;
    private static RealmConfiguration memoConfig;
    private Realm pefeRealm;
    private ArrayList<RealmAsyncTask> taskList;

    private MemoFragmentController memoDistributor;

    public RealmControllerImpl(Context context){
        this.context =context;
        realmConfig();
    }

    @Override
    public void getMemoDistributor(MemoFragmentController memoDistributor) {
        this.memoDistributor = memoDistributor;
    }
    //settings
    @RealmModule(classes={Memo.class, Directory.class, Todo.class, SelectedTodo.class})
    private class MemoModule{}

    private void realmConfig(){
        memoConfig = new RealmConfiguration.Builder()
                .name(MEMO)
                //.encryptionKey()
                .schemaVersion(NEWEST_VERSION)
                .modules(new MemoModule())
                .deleteRealmIfMigrationNeeded()
                .build();
    }
    private class PefeMigration implements RealmMigration {
        @Override
        public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        }
    }
    
    //lifeCycle
    @Override
    public void realmInit() {
        pefeRealm = Realm.getInstance(memoConfig);
        taskList = new ArrayList<>();
    }
    @Override
    public void realmClose(){
        taskClose();
        pefeRealm.close();
    }
    @Override
    public void taskClose(){
        for(RealmAsyncTask t: taskList){
            if(!t.isCancelled()){
                t.cancel();
            }
        }
    }

    @Override
    public void createDir(long order, String code, String name, String pw){
        long no = getLargestNo(RealmControllerImpl.DIR)+1;
        RealmAsyncTask addDirTask = pefeRealm.executeTransactionAsync(new DirCreateTransaction(no,order,code,name,pw)
                            ,new OnDirCreateSuccess()
                            ,new OnDirCreateError());
        taskList.add(addDirTask);
    }
    @Override
    public void modifyDir(String code,long order, String name, String pw){
        RealmAsyncTask modifyDirTask = pefeRealm.executeTransactionAsync(new DirModifyTransaction(order,code,name,pw)
                ,new OnDirModifySuccess()
                ,new OnDirModifyError());
        taskList.add(modifyDirTask);
    }
    @Override
    public void deleteDir(String code){
        RealmAsyncTask deleteDirTask = pefeRealm.executeTransactionAsync(new DirDeleteTransaction(code)
                ,new OnDirDeleteSuccess()
                ,new OnDirDeleteError());
        taskList.add(deleteDirTask);
    }

    @Override
    public OrderedRealmCollection<Directory> readDirAll() {
        RealmResults<Directory> result = pefeRealm.where(Directory.class).findAll();
        return result;
    }


    @Override
    public long getLargestNo(String whose){
        long result = 0;
        Number temp;
        switch (whose){
            case DIR:
                temp = pefeRealm.where(Directory.class).max("no");
                if(temp != null){
                    result = temp.longValue();
                }
                break;
            case MEMO:
                temp = pefeRealm.where(Memo.class).max("no");
                if(temp != null){
                    result = temp.longValue();
                }
                break;
            case TODO:
                temp = pefeRealm.where(Todo.class).max("no");
                if(temp != null){
                    result = temp.longValue();
                }
                break;
            case SELECTED_TODO:
                temp = pefeRealm.where(SelectedTodo.class).max("no");
                if(temp != null){
                    result = temp.longValue();
                }
                break;
        }
        return  result;
    }
    @Override
    public void writeMemo(boolean importance, String dirCode, String content){
        long no = getLargestNo(RealmControllerImpl.MEMO)+1;
        RealmAsyncTask writeMemoTask = pefeRealm.executeTransactionAsync(new MemoWriteTransaction(no, importance, dirCode, content)
                        , new OnMemoWriteSuccess()
                        , new OnMemoWriteError());

        taskList.add(writeMemoTask);
    }
    @Override
    public void modifyMemo(long no, boolean importance, String dirCode, String content){
        RealmAsyncTask modifyMemoTask = pefeRealm.executeTransactionAsync(new MemoModifyTransaction(no, importance, dirCode, content)
                        , new OnMemoModifySuccess()
                        , new OnMemoModifyError());
        taskList.add(modifyMemoTask);
    }
    @Override
    public void modifyMemoinEditor(long no, boolean importance, String dirCode, String content){
        pefeRealm.executeTransaction(new MemoModifyTransaction(no,importance,dirCode,content));
    }
    @Override
    public void deleteMemo(long no){
        RealmAsyncTask deleteMemoTask = pefeRealm
                .executeTransactionAsync(new MemoDeleteTransaction(no)
                        , new OnMemoDeleteSuccess()
                        , new OnMemoDeleteError());
        taskList.add(deleteMemoTask);
    }

    @Override
    public Memo readAMemoByNO(long no){
        Memo result = pefeRealm.where(Memo.class).equalTo("no",no).findFirst();
        return result;
    }
    @Override
    public OrderedRealmCollection<Memo> readMemoByImportance(String dirCode) {
        RealmResults<Memo> result = pefeRealm.where(Memo.class).equalTo("dirCode",dirCode).equalTo("important",true).findAll();
        return result;
    }
    @Override
    public OrderedRealmCollection<Memo> readMemoByDirCode(String dirCode) {
        RealmResults<Memo> result = pefeRealm.where(Memo.class).equalTo("dirCode",dirCode).findAll();
        return result;
    }
    @Override
    public OrderedRealmCollection<Memo> readMemoByContent(String keyWord, String dirCode) {
        RealmResults<Memo> result = pefeRealm.where(Memo.class).equalTo("dirCode",dirCode).contains("content",keyWord).findAll();
        return result;
    }

    @Override
    public void writeTodo(String type, String content, Date createDate) {
        long no = getLargestNo(RealmControllerImpl.TODO)+1;
        RealmAsyncTask writeTodoTask = pefeRealm.executeTransactionAsync(new TodoWriteTransaction(no,type,content,createDate)
                        ,new OnTodoWriteSuccess()
                        ,new OnTodoWriteError());
        taskList.add(writeTodoTask);
    }
    @Override
    public void modifyTodo(long no, String type, String content, Date createDate, boolean done) {
        RealmAsyncTask modifyTodoTask = pefeRealm.executeTransactionAsync(new TodoModifyTransaction(no,type,content,createDate,done)
                ,new OnTodoModifySuccess()
                ,new OnTodoModifyError());
        taskList.add(modifyTodoTask);
    }
    @Override
    public void deleteTodo(long no) {
        RealmAsyncTask deleteTodoTask = pefeRealm.executeTransactionAsync(new TodoDeleteTransaction(no)
                ,new OnTodoDeleteSuccess()
                ,new OnTodoDeleteError());
        taskList.add(deleteTodoTask);
    }

    @Override
    public Todo readATodoByNO(long no) {
        Todo todo = pefeRealm.where(Todo.class).equalTo("no",no).findFirst();
        return todo;
    }
    @Override
    public OrderedRealmCollection<Todo> readTodoByContent(String keyWord) {
        RealmResults<Todo> todos = pefeRealm.where(Todo.class).contains("content",keyWord).findAll();
        return todos;
    }
    @Override
    public OrderedRealmCollection<Todo> readTodoByType(String type) {
        RealmResults<Todo> todos = pefeRealm.where(Todo.class).contains("type",type).findAll();
        return todos;
    }

    @Override
    public void writeSelectedTodo(String type, String content, Date belongDate, Date putDate) {
        long no = getLargestNo(RealmControllerImpl.SELECTED_TODO)+1;
        RealmAsyncTask writeSelectedTodoTask = pefeRealm.executeTransactionAsync(new SelectedTodoWriteTransaction(no,type,content,belongDate,putDate)
                ,new OnSelectedTodoWriteSuccess()
                ,new OnSelectedTodoWriteError());
        taskList.add(writeSelectedTodoTask);
    }
    @Override
    public void modifySelectedTodo(long no, boolean done, String type, String content, Date belongDate, Date putDate) {
        RealmAsyncTask modifySelectedTodoTask = pefeRealm.executeTransactionAsync(new SelectedTodoModifyTransaction(no,done,type,content,belongDate,putDate)
                ,new OnSelectedTodoModifySuccess()
                ,new OnSelectedTodoModifyError());
        taskList.add(modifySelectedTodoTask);
    }
    @Override
    public void deleteSelectedTodo(long no) {
        RealmAsyncTask deleteSelectedTodoTask = pefeRealm.executeTransactionAsync(new SelectedTodoDeleteTransaction(no)
                ,new OnSelectedTodoDeleteSuccess()
                ,new OnSelectedTodoDeleteError());
        taskList.add(deleteSelectedTodoTask);
    }

    @Override
    public SelectedTodo readASelectedTodoByNO(long no) {
        SelectedTodo selectedTodo = pefeRealm.where(SelectedTodo.class).equalTo("no",no).findFirst();
        return selectedTodo;
    }
    @Override
    public OrderedRealmCollection<SelectedTodo> readSelectedTodoByContent(String keyWord) {
        RealmResults<SelectedTodo>selectedTodos = pefeRealm.where(SelectedTodo.class).equalTo("content",keyWord).findAll();
        return selectedTodos;
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

    private class MemoWriteTransaction implements Realm.Transaction {
        long no;
        boolean importance;
        String dirCode;
        String content;

        private MemoWriteTransaction(long no, boolean importance, String dirCode , String content) {
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
    private class OnMemoWriteSuccess implements Realm.Transaction.OnSuccess {

        @Override
        public void onSuccess() {
            Toast.makeText(context, "Memo Saved", Toast.LENGTH_SHORT).show();
        }
    }
    private class OnMemoWriteError implements Realm.Transaction.OnError {

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
//            String currentCode = memoDistributor.getCurrentFolderCode();
//            memoDistributor.setMemosByDirCode(Sample.defaultCode);
            Toast.makeText(context, "Memo Modified", Toast.LENGTH_SHORT).show();
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
//            String currentCode = memoDistributor.getCurrentFolderCode();
//            memoDistributor.setMemosByDirCode(currentCode);
            Toast.makeText(context, "Memo Deleted", Toast.LENGTH_SHORT).show();
        }
    }
    private class OnMemoDeleteError implements Realm.Transaction.OnError {
        @Override
        public void onError(Throwable error) {

        }
    }

    private class TodoWriteTransaction implements Realm.Transaction {
        private long no;
        private String type;
        private String content;
        private Date createDate;

        public TodoWriteTransaction(long no, String type, String content, Date createDate) {
            this.no = no;
            this.type = type;
            this.content = content;
            this.createDate = createDate;
        }

        @Override
        public void execute(Realm realm) {
            Todo newTodo = realm.createObject(Todo.class,no);
            newTodo.setType(type);
            newTodo.setContent(content);
            newTodo.setCreateDate(createDate);
            newTodo.setDone(false);
        }
    }
    private class OnTodoWriteSuccess implements Realm.Transaction.OnSuccess {

        @Override
        public void onSuccess() {
            Toast.makeText(context, "Todo Saved", Toast.LENGTH_SHORT).show();
        }
    }
    private class OnTodoWriteError implements Realm.Transaction.OnError {

        @Override
        public void onError(Throwable error) {
            error.printStackTrace();
        }
    }

    private class TodoModifyTransaction implements Realm.Transaction{
        private long no;
        private String type;
        private String content;
        private Date createDate;
        private boolean done;

        public TodoModifyTransaction(long no, String type, String content, Date createDate, boolean done) {
            this.no = no;
            this.type = type;
            this.content = content;
            this.createDate = createDate;
            this.done = done;
        }

        @Override
        public void execute(Realm realm) {
            Todo modifyTodo = realm.where(Todo.class)
                    .equalTo("no", no)
                    .findFirst();
            modifyTodo.setType(type);
            modifyTodo.setContent(content);
            modifyTodo.setCreateDate(createDate);
            modifyTodo.setDone(done);
        }
    }
    private class OnTodoModifySuccess implements Realm.Transaction.OnSuccess{

        @Override
        public void onSuccess() {
            Toast.makeText(context, "Todo Modified", Toast.LENGTH_SHORT).show();
        }
    }
    private class OnTodoModifyError implements Realm.Transaction.OnError{

        @Override
        public void onError(Throwable error) {
            error.printStackTrace();
        }
    }

    private class TodoDeleteTransaction implements Realm.Transaction {
        Long no;
        private TodoDeleteTransaction(long no) {
            this.no = no;
        }

        @Override
        public void execute(Realm realm) {
            Todo deleteTodo = realm.where(Todo.class).equalTo("no",no).findFirst();
            deleteTodo.deleteFromRealm();
        }
    }
    private class OnTodoDeleteSuccess implements Realm.Transaction.OnSuccess {
        @Override
        public void onSuccess() {
            Toast.makeText(context, "Todo Deleted", Toast.LENGTH_SHORT).show();
        }
    }
    private class OnTodoDeleteError implements Realm.Transaction.OnError {
        @Override
        public void onError(Throwable error) {

        }
    }

    private class SelectedTodoWriteTransaction implements Realm.Transaction {
        private long no;
        private String type;
        private String content;
        private Date belongDate;
        private Date putDate;

        public SelectedTodoWriteTransaction(long no, String type, String content, Date belongDate, Date putDate) {
            this.no = no;
            this.type = type;
            this.content = content;
            this.belongDate = belongDate;
            this.putDate = putDate;
        }

        @Override
        public void execute(Realm realm) {
            SelectedTodo newTodo = realm.createObject(SelectedTodo.class,no);
            newTodo.setType(type);
            newTodo.setDone(false);
            newTodo.setContent(content);
            newTodo.setBelongDate(belongDate);
            newTodo.setPutDate(putDate);
        }
    }
    private class OnSelectedTodoWriteSuccess implements Realm.Transaction.OnSuccess {

        @Override
        public void onSuccess() {
        }
    }
    private class OnSelectedTodoWriteError implements Realm.Transaction.OnError {

        @Override
        public void onError(Throwable error) {
            error.printStackTrace();
        }
    }

    private class SelectedTodoModifyTransaction implements Realm.Transaction{
        private long no;
        private boolean done;
        private String type;
        private String content;
        private Date belongDate;
        private Date putDate;

        public SelectedTodoModifyTransaction(long no, boolean done, String type, String content, Date belongDate, Date putDate) {
            this.no = no;
            this.done = done;
            this.type = type;
            this.content = content;
            this.belongDate = belongDate;
            this.putDate = putDate;
        }

        @Override
        public void execute(Realm realm) {
            SelectedTodo modifySelectedTodo = realm.where(SelectedTodo.class)
                    .equalTo("no", no)
                    .findFirst();
            modifySelectedTodo.setType(type);
            modifySelectedTodo.setContent(content);
            modifySelectedTodo.setBelongDate(belongDate);
            modifySelectedTodo.setBelongDate(putDate);
            modifySelectedTodo.setDone(done);
        }
    }
    private class OnSelectedTodoModifySuccess implements Realm.Transaction.OnSuccess{

        @Override
        public void onSuccess() {
            //directory 내 Board 새로고침
        }
    }
    private class OnSelectedTodoModifyError implements Realm.Transaction.OnError{

        @Override
        public void onError(Throwable error) {
            error.printStackTrace();
        }
    }

    private class SelectedTodoDeleteTransaction implements Realm.Transaction {
        Long no;
        private SelectedTodoDeleteTransaction(long no) {
            this.no = no;
        }

        @Override
        public void execute(Realm realm) {
            SelectedTodo deleteSelectedTodo = realm.where(SelectedTodo.class).equalTo("no",no).findFirst();
            deleteSelectedTodo.deleteFromRealm();
        }
    }
    private class OnSelectedTodoDeleteSuccess implements Realm.Transaction.OnSuccess {
        @Override
        public void onSuccess() {

        }
    }
    private class OnSelectedTodoDeleteError implements Realm.Transaction.OnError {
        @Override
        public void onError(Throwable error) {

        }
    }


}
