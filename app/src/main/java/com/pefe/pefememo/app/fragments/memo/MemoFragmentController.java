package com.pefe.pefememo.app.fragments.memo;

/**
 * Created by dodoproject on 2016-11-25.
 */

public interface MemoFragmentController {
    void setCustomResult(String keyWord);
    void addFolder(int position);
    void addFolder(long no, long order, String dirCode,String name, String pw);

    String getCurrentFolderCode();
    void setMemosByDirCode(String code);
}
