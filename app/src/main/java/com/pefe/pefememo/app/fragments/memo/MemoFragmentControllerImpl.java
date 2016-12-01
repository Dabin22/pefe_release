package com.pefe.pefememo.app.fragments.memo;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.pefe.pefememo.realm.RealmController;
import com.pefe.pefememo.model.directory.Directory;
import com.pefe.pefememo.model.memo.Memo;
import com.pefe.pefememo.sample.Sample;

import java.util.Calendar;
import java.util.Date;

import io.realm.OrderedRealmCollection;
import io.realm.Sort;

/**
 * Created by dodoproject on 2016-11-25.
 */

public class MemoFragmentControllerImpl implements MemoFragmentController {
    Context context;
    RealmController realmController = null;
    RecyclerView memoRecyclerView,dirRecyclerView;
    DirViewAdapter dirViewAdapter;
    MemoViewAdapter memoViewAdapter;
    OrderedRealmCollection<Memo> memos = null;
    OrderedRealmCollection<Directory> directories = null;

    private String currentFolderCode = null;

    public MemoFragmentControllerImpl(){}

    public MemoFragmentControllerImpl(Context context, RealmController realmController, RecyclerView memoRecyclerView, RecyclerView dirRecyclerView) {
        this.context = context;
        this.realmController = realmController;
        this.memoRecyclerView = memoRecyclerView;
        this.dirRecyclerView = dirRecyclerView;
        setDirRecyclerView();
        //Directory first =realmController.readDirFirst();
        String firstCode = "";
        setMemosByDirCode(firstCode);
    }
    @Override
    public void setCustomResult(String keyWord){
        memos = realmController.readMemoByContent(keyWord,currentFolderCode);
        MemoViewAdapter memoAdapter = new MemoViewAdapter(context,memos,true, realmController);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context,1);
        memoRecyclerView.setAdapter(memoAdapter);
        memoRecyclerView.setLayoutManager(gridLayoutManager);
    }

    @Override
    public void setTrashCan() {
        memos = realmController.readDeletedMemo().sort("deletedDate", Sort.DESCENDING);
        MemoViewAdapter memoAdapter = new MemoViewAdapter(context,memos,true,realmController);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context,1);
        memoRecyclerView.setAdapter(memoAdapter);
        memoRecyclerView.setLayoutManager(gridLayoutManager);
    }


    @Override
    public void addFolder(int position){
        Directory d1 = Sample.getDirectories().get(position);
        Date nDate = Calendar.getInstance().getTime();
        realmController.createDir(d1.getName(),d1.getPw(),nDate);
    }
    @Override
    public void addFolder(long no, long order, String dirCode,String name, String pw){
        Date nDate = Calendar.getInstance().getTime();
        realmController.createDir(name,pw,nDate);
    }

    @Override
    public void setMemosByDirCode(String code){
        currentFolderCode = code;
        memos = realmController.readMemoByDirCode(code).sort("important",Sort.DESCENDING,"no",Sort.DESCENDING);
        MemoViewAdapter memoAdapter = new MemoViewAdapter(context,memos,true, realmController);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context,1);
        memoRecyclerView.setAdapter(memoAdapter);
        memoRecyclerView.setLayoutManager(gridLayoutManager);
    }
    @Override
    public String getCurrentFolderCode() {
        return currentFolderCode;
    }

    private void setDirRecyclerView(){
        directories = realmController.readDirAll();
        DirViewAdapter dirViewAdapter = new DirViewAdapter(context,directories,true,MemoFragmentControllerImpl.this, realmController);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context,1);
        dirRecyclerView.setAdapter(dirViewAdapter);
        dirRecyclerView.setLayoutManager(gridLayoutManager);
    }
}
