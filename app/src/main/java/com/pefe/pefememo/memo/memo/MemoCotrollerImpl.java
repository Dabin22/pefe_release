package com.pefe.pefememo.memo.memo;

import com.pefe.pefememo.model.directory.Directory;
import com.pefe.pefememo.model.memo.Memo;
import com.pefe.pefememo.model.todo.Todo;
import com.pefe.pefememo.realm.RealmController;
import com.pefe.pefememo.realm.RealmControllerImpl;

import java.util.Calendar;
import java.util.Date;

import io.realm.OrderedRealmCollection;

/**
 * Created by dodoproject on 2016-11-24.
 */

public class MemoCotrollerImpl implements MemoController {
    RealmController realmController;

    public MemoCotrollerImpl(RealmController realmController) {
        this.realmController = realmController;
    }

    @Override
    public String getDir(String name) {
        String result = null;
        result = realmController.readADir(name).getCode();
        return result;
    }

    @Override
    public OrderedRealmCollection<Directory> getDirs() {
        OrderedRealmCollection<Directory> result = realmController.readDirAll();
        return result;
    }


    @Override
    public void saveMemo(boolean importance, String dirCode, String title,String content){
//        long no = realmController.getLargestNo(RealmControllerImpl.MEMO)+1;
        Date nDate = Calendar.getInstance().getTime();
        realmController.writeMemo(importance,dirCode,title,content,nDate);
    }

    @Override
    public void saveTodo(boolean repeat, String content) {
//        long no = realmController.getLargestNo(RealmControllerImpl.TODO)+1;
        String type = null;
        if(repeat){
            type = Todo.REPEAT;
        }else{
            type = Todo.ONCE;
        }
        //Todo DateType?
        Date currentDate = Calendar.getInstance().getTime();
        realmController.writeTodo(type,content,currentDate);
    }
}
