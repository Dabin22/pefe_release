package com.pefe.pefememo.app.fragments.memo;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pefe.pefememo.R;
import com.pefe.pefememo.realm.RealmController;
import com.pefe.pefememo.model.directory.Directory;

import java.util.Calendar;
import java.util.Date;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.Sort;

/**
 * Created by dodoproject on 2016-11-25.
 */
//todo 디렉토리 삭제 방법 고민하기
public class DirViewAdapter extends RealmRecyclerViewAdapter<Directory,DirViewAdapter.ViewHolder> {

    private RealmController realmController;
    private MemoFragmentController memoDistributor;
    private OrderedRealmCollection<Directory> datas;

    public DirViewAdapter(@NonNull Context context, @Nullable OrderedRealmCollection<Directory> data, boolean autoUpdate,MemoFragmentControllerImpl memoDistributor ,RealmController realmController) {
        super(context, data, autoUpdate);
        datas = data.sort("order", Sort.ASCENDING);
        this.realmController = realmController;
        this.memoDistributor = memoDistributor;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //View dir = View.inflate(parent.getContext(),R.layout.item_directory,null);
        View dir = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_directory,parent,false);
        return new ViewHolder(dir);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        String name = datas.get(position).getName();
        String dirCode = datas.get(position).getCode();
        String dirPw = datas.get(position).getPw();

        holder.dirBtn.setOnClickListener(new dirClickListener(dirCode,dirPw));
        holder.dirBtn.setOnLongClickListener(new dirLongClickListener(dirCode,dirPw));
        holder.dirName.setText(name);

    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        Button dirBtn;
        TextView dirName;

        private ViewHolder(View itemView) {
            super(itemView);
            dirBtn = (Button) itemView.findViewById(R.id.dirBtn);
            dirName = (TextView) itemView.findViewById(R.id.dirName);
        }
    }
    private class DirLongClickListener implements  View.OnLongClickListener{
        String name,code,pw;

        private DirLongClickListener(String name, String code, String pw) {
            this.name = name;
            this.code = code;
            this.pw = pw;
        }
        @Override
        public boolean onLongClick(View view) {
            if(pw.equals("")){
                AlertDialog modifyDirDialog = modifyDirCreateDialog(code);
                modifyDirDialog.show();
            }
            else{
                askPW(pw,code,true);
            }
            return true;
        }
    }
    private class dirClickListener implements View.OnClickListener {
        String code;
        String pw;

        private DirCheckedChangeListener(String code, String pw) {
            this.code = code;
            this.pw = pw;
        }
        @Override
        public void onClick(View view) {
            if(pw.equals("")){
                memoDistributor.setMemosByDirCode(code);
            }
            else{
                askPW(pw, code,false);
            }
        }
    }
    private void askPW(String pw, String code, boolean edit){
        AlertDialog pwDialog = createPWDialog(pw,code,edit);
        pwDialog.show();
    }
    private AlertDialog createPWDialog(String pw, String code,boolean edit){
        final LinearLayout windowPw = (LinearLayout) View.inflate(context,R.layout.dialog_pw,null);
        AlertDialog pwDialog = new AlertDialog.Builder(context)
                .setView(windowPw)
                .setTitle("Password")
                .setPositiveButton("Open", new PWDialogOnClickListener(windowPw, pw, code, edit))
                .setCancelable(true)
                .create();
        return pwDialog;
    }

    private class PWDialogOnClickListener implements DialogInterface.OnClickListener{
        String pw, code;
        View windowPw = null;
        boolean edit;
        PWDialogOnClickListener(View windowPw, String pw, String code, boolean edit){
            this.pw = pw;
            this.code = code;
            this.windowPw = windowPw;
            this.edit = edit;
        }
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            EditText pwInput =(EditText) windowPw.findViewById(R.id.edtPW);
            String inputPW = pwInput.getText().toString();
            if(pw.equals(inputPW)){
                if(!edit){
                    dialogInterface.dismiss();
                    memoDistributor.setMemosByDirCode(code);
                }else{
                    dialogInterface.dismiss();
                    AlertDialog modifyDirDialog = modifyDirCreateDialog(code);
                    modifyDirDialog.show();
                }
            }else{
                Toast.makeText(context, "잘못된 비밀번호입니다", Toast.LENGTH_SHORT).show();
                pwInput.setText("");
            }
        }
    }
    private AlertDialog modifyDirCreateDialog(String dirCode) {
        final LinearLayout windowCreateDir = (LinearLayout) View.inflate(context, R.layout.dialog_create_dir, null);
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(windowCreateDir)
                .setTitle("Modify Folder")
                .setNegativeButton("Cancel", new DirDialogCancelListener())
                .setPositiveButton("Modify", new DirDialogOnClickListener(windowCreateDir,dirCode))
                .setNeutralButton("Delete", new DirDialogDeleteListener(dirCode))
                .setCancelable(true)
                .create();
        return dialog;
    }

    private class DirDialogOnClickListener implements DialogInterface.OnClickListener {
        LinearLayout windowCreateDir = null;
        String dirCode;

        DirDialogOnClickListener(LinearLayout windowCreateDir, String dirCode) {
            this.windowCreateDir = windowCreateDir;
            this.dirCode = dirCode;
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
                    realmController.modifyDir(dirCode,inputName,inputPw);
                } else {
                    Toast.makeText(context, "Please recheck password", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "More than one letter is required for name", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private class DirDialogCancelListener implements DialogInterface.OnClickListener {

        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.dismiss();
        }
    }
    private class DirDialogDeleteListener implements DialogInterface.OnClickListener {
        String dirCode;

        public DirDialogDeleteListener(String dirCode) {
            this.dirCode = dirCode;
        }

        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            AlertDialog deleteDialog = new AlertDialog.Builder(context)
                    .setTitle("Delete Folder")
                    .setMessage("Folder is not recoverable, Do you really want to delete?")
                    .setMessage("(Memo will be in trash can.)")
                    .setNegativeButton("Cancel", new DirDialogCancelListener())
                    .setNeutralButton("Delete", new DeleteDirConfirmedListener(dirCode))
                    .setCancelable(true)
                    .create();
            deleteDialog.show();
        }
        private class DeleteDirConfirmedListener implements DialogInterface.OnClickListener{
            String dirCode;

            public DeleteDirConfirmedListener(String dirCode) {
                this.dirCode = dirCode;
            }

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Date nDate = Calendar.getInstance().getTime();
                realmController.deleteDir(dirCode,nDate);
                dialogInterface.dismiss();
            }
        }
    }
}
