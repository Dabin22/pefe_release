package com.pefe.pefememo.Realm;

import android.view.View;

/**
 * Created by dodoproject on 2016-11-22.
 */

public interface RealmControl {
    void realmInit();
    void writeMemo(View innerMemo, long no, boolean importance, String dirCode, String content);
    void modifyMemo(long no, boolean importance, String dirCode, String content);
    void deleteMemo(long no);
}
