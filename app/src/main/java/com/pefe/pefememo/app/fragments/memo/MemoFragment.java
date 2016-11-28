package com.pefe.pefememo.app.fragments.memo;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pefe.pefememo.R;
import com.pefe.pefememo.Realm.RealmControl;
import com.pefe.pefememo.Realm.RealmControlImpl;


public class MemoFragment extends Fragment {

    MemoFragmentController memoFragmentController;
    RealmControl realmControl;
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
        memoView =(RecyclerView)memoFragment.findViewById(R.id.memoRecyclerView);
        dirView =(RecyclerView)memoFragment.findViewById(R.id.dirRecyclerView);
        realmControl = new RealmControlImpl(getContext());
        realmControl.realmInit();
        memoFragmentController = new MemoFragmentControllerImpl(getContext(),realmControl,memoView,dirView);

        return memoFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        }

    @Override
    public void onDetach() {
        super.onDetach();
        realmControl.realmClose();
        realmControl = null;
    }
}
