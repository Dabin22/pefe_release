package com.pefe.pefememo.app.fragments.memo;

import android.content.Context;
import android.content.DialogInterface;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.pefe.pefememo.R;
import com.pefe.pefememo.realm.RealmController;
import com.pefe.pefememo.realm.RealmControllerImpl;

import java.util.Date;


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
        searchBtn.setOnClickListener(new SearchBtnClickListener(searchBar));
        ImageButton folderBtn = (ImageButton) memoFragment.findViewById(R.id.folderBtn);
        RelativeLayout folderList = (RelativeLayout) memoFragment.findViewById(R.id.folderList);
        folderBtn.setOnClickListener(new FolderBtnClickListener(folderList));
//        ToggleButton space1 = (ToggleButton) memoFragment.findViewById(R.id.space1);
//        ToggleButton space2 = (ToggleButton) memoFragment.findViewById(R.id.space2);
//
//        space1.setOnCheckedChangeListener(new FolderBtnClickListener(folderList));
//        space2.setOnCheckedChangeListener(new FolderBtnClickListener(folderList));

        Button addFolder = (Button) memoFragment.findViewById(R.id.addFolderBtn);
        addFolder.setOnClickListener(new AddFolderBtnClickListener() );
        Button noFolderBtn = (Button) memoFragment.findViewById(R.id.noFolderBtn);
        noFolderBtn.setOnClickListener(new FolderClickListener());
        Button trashCanBtn =(Button) memoFragment.findViewById(R.id.trashCanBtn);
        trashCanBtn.setOnClickListener(new FolderClickListener());
        trashCanBtn.setOnLongClickListener(new TrashCanLongClickListener());

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
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        memoFragmentController = null;
        if(realmController!=null){
            realmController.realmClose();
        }
        realmController = null;
        super.onDestroy();
    }

    private class FolderBtnClickListener implements View.OnClickListener{
        RelativeLayout folderList = null;
        boolean open = false;
        public FolderBtnClickListener(RelativeLayout folderList) {
            this.folderList = folderList;
        }

        @Override
        public void onClick(View view) {
            open = !open;
            if(open){
                ImageButton imgBtn = (ImageButton)view;
                imgBtn.setImageResource(R.drawable.folder_btn_open);
                folderList.setVisibility(View.VISIBLE);
            }else{
                ImageButton imgBtn = (ImageButton)view;
                imgBtn.setImageResource(R.drawable.folder_btn_close);
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

    private class AddFolderBtnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            AlertDialog dirCreateDialog = createDirCreateDialog();
            dirCreateDialog.show();

        }

        private AlertDialog createDirCreateDialog() {
            final LinearLayout windowCreateDir = (LinearLayout) View.inflate(getContext(), R.layout.dialog_create_dir, null);
            AlertDialog dialog = new AlertDialog.Builder(getContext())
                    .setView(windowCreateDir)
                    .setTitle("New Folder")
                    .setNegativeButton("Cancel", new DirDialogCancelListener())
                    .setPositiveButton("Create", new DirDialogOnClickListener(windowCreateDir))
                    .setCancelable(true)
                    .create();
            return dialog;
        }

        private class DirDialogOnClickListener implements DialogInterface.OnClickListener {
            LinearLayout windowCreateDir = null;

            DirDialogOnClickListener(LinearLayout windowCreateDir) {
                this.windowCreateDir = windowCreateDir;
            }

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                EditText name = (EditText) windowCreateDir.findViewById(R.id.createDirName);
                EditText pw = (EditText) windowCreateDir.findViewById(R.id.createDirPw);
                EditText pwConfirm = (EditText) windowCreateDir.findViewById(R.id.createDirPwConf);
                String inputName = name.getText().toString();
                String inputPw = pw.getText().toString();
                String inputPwConfirm = pwConfirm.getText().toString();

                if (!inputName.isEmpty()) {
                    if (inputPw.equals(inputPwConfirm)) {
                        Date nDate = java.util.Calendar.getInstance().getTime();
                        realmController.createDir(inputName, inputPw, nDate);
                    } else {
                        Toast.makeText(getContext(), "Please recheck password", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "More than one letter is required for name", Toast.LENGTH_SHORT).show();
                }
            }
        }
        private class DirDialogCancelListener implements DialogInterface.OnClickListener {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }
    }
    private class FolderClickListener implements  View.OnClickListener{
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.noFolderBtn:
                    memoFragmentController.setMemosByDirCode("");
                    break;
                case R.id.trashCanBtn:
                    memoFragmentController.setTrashCan();
                    break;
            }
        }
    }
    private class TrashCanLongClickListener implements View.OnLongClickListener{

        @Override
        public boolean onLongClick(View view) {
            AlertDialog alertDialog = emptyTrashCanDialog();
            alertDialog.show();
            return true;
        }
        private AlertDialog emptyTrashCanDialog(){
            AlertDialog dialog = new AlertDialog.Builder(getContext())
                    .setTitle("Empty TrashCan")
                    .setMessage("Memos cannot be recovered.")
                    .setNegativeButton("Cancel", new EmptyTrashCanDialogCancel())
                    .setPositiveButton("Empty", new EmptyTrashCanDialogConfirm())
                    .setCancelable(true)
                    .create();
            return dialog;
        }
        private class EmptyTrashCanDialogConfirm implements DialogInterface.OnClickListener {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                realmController.emptyTrashCan();
            }
        }
        private class EmptyTrashCanDialogCancel implements DialogInterface.OnClickListener {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }
    }
}
