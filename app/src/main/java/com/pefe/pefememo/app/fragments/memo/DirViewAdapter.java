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

    private class dirClickListener implements View.OnClickListener {
        String code;
        String pw;

        private dirClickListener(String code, String pw) {
            this.code = code;
            this.pw = pw;
        }
        @Override
        public void onClick(View view) {
            if(pw.equals("")){
                memoDistributor.setMemosByDirCode(code);
            }
            else{
                askPW(pw, code);
            }
        }
    }
    private void askPW(String pw, String code){
        AlertDialog pwDialog = createPWDialog(pw,code);
        pwDialog.show();
    }
    private AlertDialog createPWDialog(String pw, String code){
        final LinearLayout windowPw = (LinearLayout) View.inflate(context,R.layout.dialog_pw,null);
        AlertDialog pwDialog = new AlertDialog.Builder(context)
                .setView(windowPw)
                .setTitle("Password")
                .setPositiveButton("Open", new PWDialogOnClickListener(windowPw, pw, code))
                .setCancelable(true)
                .create();
        return pwDialog;
    }

    private class PWDialogOnClickListener implements DialogInterface.OnClickListener{
        String pw, code;
        View windowPw = null;
        PWDialogOnClickListener(View windowPw, String pw, String code){
            this.pw = pw;
            this.code = code;
            this.windowPw = windowPw;
        }
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            EditText pwInput =(EditText) windowPw.findViewById(R.id.edtPW);
            String inputPW = pwInput.getText().toString();
            if(pw.equals(inputPW)){
                dialogInterface.dismiss();
                memoDistributor.setMemosByDirCode(code);
            }else{
                Toast.makeText(context, "잘못된 비밀번호입니다", Toast.LENGTH_SHORT).show();
                pwInput.setText("");
            }
        }
    }
}
