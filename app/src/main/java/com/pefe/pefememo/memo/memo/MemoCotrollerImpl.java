package com.pefe.pefememo.memo.memo;

import com.pefe.pefememo.realm.RealmController;
import com.pefe.pefememo.realm.RealmControllerImpl;

/**
 * Created by dodoproject on 2016-11-24.
 */

public class MemoCotrollerImpl implements MemoController {
    RealmController realmController;

    public MemoCotrollerImpl(RealmController realmController) {
        this.realmController = realmController;
    }
    @Override
    public void saveMemo(boolean importance, String dirCode, String content){
        long no = realmController.getLargestNo(RealmControllerImpl.MEMO)+1;
        realmController.writeMemo(no,importance,dirCode,content);
    }

}
