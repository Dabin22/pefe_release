package com.pefe.pefememo.memo.memo;

import com.pefe.pefememo.model.directory.Directory;
import com.pefe.pefememo.model.memo.Memo;

import io.realm.OrderedRealmCollection;

/**
 * Created by dodoproject on 2016-11-24.
 */

public interface MemoController {
    String getDir(String name);
    OrderedRealmCollection<Directory> getDirs();
    void saveMemo(boolean importance, String dirCode, String content);
    void saveTodo(boolean repeatOnce, String content);

}
