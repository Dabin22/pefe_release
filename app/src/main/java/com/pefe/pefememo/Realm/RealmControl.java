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
    void realmClose();
    void addDir(long no, long order, String code, String name, String pw);
    void modifyDir(String code, long order, String name, String pw);
    void deleteDir(String code);
    void writeMemo(long no, boolean importance, String dirCode, String content);
    void modifyMemo(long no, boolean importance, String dirCode, String content);
    void deleteMemo(long no);
    Memo readAMemoByNO(long no);
    OrderedRealmCollection<Memo> readMemoByImportance(String dirCode);
    OrderedRealmCollection<Memo> readMemoByDirCode(String dirCode);
    OrderedRealmCollection<Memo> readMemoByContent(String keyWord, String dirCode);
    OrderedRealmCollection<Directory> readDirAll();
}
