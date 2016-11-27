package com.pefe.pefememo.app.fragments.memo;

import android.support.v7.widget.RecyclerView;

import com.pefe.pefememo.Realm.RealmControl;
import com.pefe.pefememo.model.directory.Directory;
import com.pefe.pefememo.model.memo.Memo;

import io.realm.OrderedRealmCollection;

/**
 * Created by dodoproject on 2016-11-25.
 */

public class MemoFragmentControllerImpl implements MemoFragmentController{

    RealmControl realmControl = null;
    RecyclerView memoRecyclerView,dirRecyclerView;
    DirViewAdapter dirViewAdapter;
    MemoViewAdapter memoViewAdapter;
    OrderedRealmCollection<Memo> memos = null;
    OrderedRealmCollection<Directory> directories = null;

    public MemoFragmentControllerImpl(RealmControl realmControl, RecyclerView memoRecyclerView, RecyclerView dirRecyclerView) {
        this.realmControl = realmControl;
        this.memoRecyclerView = memoRecyclerView;
        this.dirRecyclerView = dirRecyclerView;
       // memos = realmControl.readMemoByDirCode();
    }
    public void setUpRecyclerViews(){

       // memoRecyclerView.setAdapter();
    }

}
