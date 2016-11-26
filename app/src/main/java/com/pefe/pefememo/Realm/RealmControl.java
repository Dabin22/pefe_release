package com.pefe.pefememo.Realm;

import android.view.View;

import com.pefe.pefememo.model.directory.Directory;
import com.pefe.pefememo.model.memo.Memo;

import io.realm.OrderedRealmCollection;

/**
 * Created by dodoproject on 2016-11-22.
 */

public interface RealmControl {
    void realmInit();
    void writeMemo(View innerMemo, long no, boolean importance, String dirCode, String content);
    void modifyMemo(long no, boolean importance, String dirCode, String content);
    void deleteMemo(long no);
    OrderedRealmCollection<Memo> readMemoByImportance(String dirCode);
    OrderedRealmCollection<Memo> readMemoByDirCode(String dirCode);
    OrderedRealmCollection<Memo> readMemoByContent(String keyWord, String dirCode);
    OrderedRealmCollection<Directory> readDirAll();
}
