package com.pefe.pefememo.realm;

import com.pefe.pefememo.app.fragments.memo.MemoFragmentController;
import com.pefe.pefememo.model.directory.Directory;
import com.pefe.pefememo.model.memo.Memo;
import com.pefe.pefememo.model.todo.SelectedTodo;
import com.pefe.pefememo.model.todo.Todo;

import java.util.Date;

import io.realm.OrderedRealmCollection;

/**
 * Created by dodoproject on 2016-11-22.
 */

public interface RealmController {
    void getMemoDistributor(MemoFragmentController memoDistributor);
    void realmInit();
    void realmClose();
    void taskClose();


    void createDir(String name, String pw, Date createDate);
    void modifyDir(String code, long order, String name, String pw);
    void modifyDir(String code, String name, String pw);
    void deleteDir(String code, Date deletedDate);

    void createDirAsync(long order, String code, String name, String pw, Date createDate);
    void modifyDirAsync(String code, long order, String name, String pw);
    void deleteDirAsync(String code, Date deletedDate);
    OrderedRealmCollection<Directory> readDirAll();
    Directory readADir(String name);


    long getLargestNo(String whose);


    void writeMemo( boolean importance,String title ,String dirCode, String content, Date createDate);
    void modifyMemo(long no, boolean importance, String title, String dirCode, String content);
    void deleteMemo(long no, Date deletedDate);
    void deleteMemoForever(long no);
    void emptyTrashCan();
    void recycleMemo(long no);
    void recycleAll();

    void writeMemoAsync( boolean importance, String dirCode, String title,String content, Date createDate);
    void modifyMemoAsync(long no, boolean importance, String dirCode, String title,String content);
    void deleteMemoAsync(long no, Date deletedDate);
    void emptyTrashCanAsync();
    void recycleAllAsync();

    Memo readAMemoByNO(long no);
    OrderedRealmCollection<Memo> readMemoByImportance(String dirCode);
    OrderedRealmCollection<Memo> readMemoByDirCode(String dirCode);
    OrderedRealmCollection<Memo> readMemoByContent(String keyWord, String dirCode);
    OrderedRealmCollection<Memo> readDeletedMemo();

    void writeTodo(String type, String content, Date createDate);
    void modifyTodo(long no, String type, String content, Date createDate, boolean done);
    void deleteTodo(long no);

    void writeTodoAsync(String type, String content, Date createDate);
    void modifyTodoAsync(long no, String type, String content, Date createDate, boolean done);
    void deleteTodoAsync(long no);

    Todo readATodoByNO(long no);
    OrderedRealmCollection<Todo> readTodoByContent(String keyWord);
    OrderedRealmCollection<Todo> readTodoByType(String type);


    void writeSelectedTodo(long todoNo,String type, String content, Date belongDate, Date putDate);
    void modifySelectedTodo(long no, boolean done, String type, String content, Date belongDate, Date putDate);
    void deleteSelectedTodo(long no);

    void writeSelectedTodoAsync(String type, String content, Date belongDate, Date putDate);
    void modifySelectedTodoAsync(long no, boolean done, String type, String content, Date belongDate, Date putDate);
    void deleteSelectedTodoAsync(long no);

    SelectedTodo readASelectedTodoByNO(long no);
    OrderedRealmCollection<SelectedTodo> readSelectedTodoByContent(String keyWord);
    OrderedRealmCollection<SelectedTodo> readSelectedTodoByBelongDate(Date date);
}
