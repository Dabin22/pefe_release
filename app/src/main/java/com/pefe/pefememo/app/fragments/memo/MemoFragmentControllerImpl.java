package com.pefe.pefememo.app.fragments.memo;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.pefe.pefememo.Realm.RealmControl;
import com.pefe.pefememo.model.directory.Directory;
import com.pefe.pefememo.model.memo.Memo;
import com.pefe.pefememo.sample.Sample;

import io.realm.OrderedRealmCollection;

/**
 * Created by dodoproject on 2016-11-25.
 */

public class MemoFragmentControllerImpl implements MemoFragmentController, MemoDistributor{
    Context context;
    RealmControl realmControl = null;
    RecyclerView memoRecyclerView,dirRecyclerView;
    DirViewAdapter dirViewAdapter;
    MemoViewAdapter memoViewAdapter;
    OrderedRealmCollection<Memo> memos = null;
    OrderedRealmCollection<Directory> directories = null;

    public MemoFragmentControllerImpl(){}

    public MemoFragmentControllerImpl(Context context, RealmControl realmControl, RecyclerView memoRecyclerView, RecyclerView dirRecyclerView) {
        this.context = context;
        this.realmControl = realmControl;
        this.memoRecyclerView = memoRecyclerView;
        this.dirRecyclerView = dirRecyclerView;
        setDirRecyclerView();
        setMemosByDirCode(Sample.defaultCode);
    }

    @Override
    public void setMemosByDirCode(String code){
        memos = realmControl.readMemoByDirCode(code);
        MemoViewAdapter memoAdapter = new MemoViewAdapter(context,memos,true,realmControl);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context,1);
        memoRecyclerView.setAdapter(memoAdapter);
        memoRecyclerView.setLayoutManager(gridLayoutManager);

    }
    public void setDirRecyclerView(){
        directories = realmControl.readDirAll();
        DirViewAdapter dirViewAdapter = new DirViewAdapter(context,directories,true,MemoFragmentControllerImpl.this,realmControl);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context,1);
        dirRecyclerView.setAdapter(dirViewAdapter);
        dirRecyclerView.setLayoutManager(gridLayoutManager);
    }


}
