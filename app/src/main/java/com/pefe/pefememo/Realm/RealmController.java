package com.pefe.pefememo.realm;

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
    void realmInit();
    void realmClose();
    void taskClose();
    
    void createDir(long no, long order, String code, String name, String pw);
    void modifyDir(String code, long order, String name, String pw);
    void deleteDir(String code);
    OrderedRealmCollection<Directory> readDirAll();
    
    long getLargestNo(String whose);
    void writeMemo(long no, boolean importance, String dirCode, String content);
    void modifyMemo(long no, boolean importance, String dirCode, String content);
    void deleteMemo(long no);
    Memo readAMemoByNO(long no);
    OrderedRealmCollection<Memo> readMemoByImportance(String dirCode);
    OrderedRealmCollection<Memo> readMemoByDirCode(String dirCode);
    OrderedRealmCollection<Memo> readMemoByContent(String keyWord, String dirCode);
    
    void writeTodo(long no, String type, String content, Date createDate);
    void modifyTodo(long no, String type, String content, Date createDate, boolean done);
    void deleteTodo(long no);
    Todo readATodoByNO(long no);
    OrderedRealmCollection<Todo> readTodoByContent(String keyWord);

    void writeSelectedTodo( long no, String type, String content, Date belongDate, Date putDate);
    void modifySelectedTodo(long no, boolean done, String type, String content, Date belongDate, Date putDate);
    void deleteSelectedTodo(long no);
    SelectedTodo readASelectedTodoByNO(long no);
    OrderedRealmCollection<SelectedTodo> readSelectedTodoByContent(String keyWord);
}