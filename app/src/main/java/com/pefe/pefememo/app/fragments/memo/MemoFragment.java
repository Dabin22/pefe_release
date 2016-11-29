package com.pefe.pefememo.app.fragments.memo;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.ToggleButton;

import com.pefe.pefememo.R;
import com.pefe.pefememo.realm.RealmController;
import com.pefe.pefememo.realm.RealmControllerImpl;


public class MemoFragment extends Fragment {

    MemoFragmentController memoFragmentController;
    RealmController realmController;
    RecyclerView memoView, dirView;

    public MemoFragment() {
        // Required empty public constructor
    }
    public static MemoFragment newInstance() {
        MemoFragment fragment = new MemoFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View memoFragment = inflater.inflate(R.layout.fragment_memo, container, false);

        EditText searchBar = (EditText)memoFragment.findViewById(R.id.searchBar);
        Button searchBtn = (Button)memoFragment.findViewById(R.id.searchBtn);
        ToggleButton folderBtn = (ToggleButton)memoFragment.findViewById(R.id.folderBtn);
        ScrollView folderList = (ScrollView)memoFragment.findViewById(R.id.folderList);
        searchBtn.setOnClickListener(new SearchBtnClickListener(searchBar));
        folderBtn.setOnCheckedChangeListener(new FolderBtnClickListener(folderList));

        Button addFolder = (Button) memoFragment.findViewById(R.id.addFolderBtn);
        addFolder.setOnClickListener(new AddFolderBtnClickListener() );

        memoView =(RecyclerView)memoFragment.findViewById(R.id.memoRecyclerView);
        dirView =(RecyclerView)memoFragment.findViewById(R.id.dirRecyclerView);
        realmController = new RealmControllerImpl(getContext());
        realmController.realmInit();
        memoFragmentController = new MemoFragmentControllerImpl(getContext(), realmController,memoView,dirView);
        realmController.getMemoDistributor(memoFragmentController);
        return memoFragment;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        }

    @Override
    public void onDetach() {
        memoFragmentController = null;
        realmController.realmClose();
        realmController = null;
        super.onDetach();
    }
    private class FolderBtnClickListener implements CompoundButton.OnCheckedChangeListener{
        ScrollView folderList = null;

        public FolderBtnClickListener(ScrollView folderList) {
            this.folderList = folderList;
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if(b){
                folderList.setVisibility(View.VISIBLE);
            }else{
                folderList.setVisibility(View.GONE);
            }
        }
    }

    private class SearchBtnClickListener implements View.OnClickListener{
        EditText searchBar = null;

        public SearchBtnClickListener(EditText searchBar) {
            this.searchBar = searchBar;
        }

        @Override
        public void onClick(View view) {
            String keyWord = searchBar.getText().toString();
            memoFragmentController.setCustomResult(keyWord);
        }
    }

    private class AddFolderBtnClickListener implements View.OnClickListener{
        int i = 0;
        @Override
        public void onClick(View view) {
            if(i<2){
            memoFragmentController.addFolder(i);
            i++;}
        }
    }
}
