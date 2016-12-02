package com.pefe.pefememo.app.fragments.memo;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.pefe.pefememo.R;
import com.pefe.pefememo.realm.RealmController;
import com.pefe.pefememo.app.editor.MemoEditorViewImpl;
import com.pefe.pefememo.model.memo.Memo;
import com.pefe.pefememo.tools.CopyTool;

import java.util.Calendar;
import java.util.Date;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.Sort;

/**
 * Created by dodoproject on 2016-11-25.
 */

public class MemoViewAdapter extends RealmRecyclerViewAdapter<Memo,MemoViewAdapter.ViewHolder> {
    Context context;
    RealmController realmController = null;

    private OrderedRealmCollection<Memo> datas = null;
    private final int MINLINES =3;
    private final int MAXLINES = 6;

    public MemoViewAdapter(@NonNull Context context, @Nullable OrderedRealmCollection<Memo> data,
                           boolean autoUpdate, RealmController realmController) {
        super(context, data, autoUpdate);
        datas = data.sort("no", Sort.DESCENDING);
        this.context = context;
        this.realmController = realmController;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View memo = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_memo,parent,false);
        return new ViewHolder(memo);
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        long no = datas.get(position).getNo();
        boolean isImportant = datas.get(position).isImportant();
        boolean isDeleted = datas.get(position).isDeleted();
        String dirCode = datas.get(position).getDirCode();
        String memoTitle = datas.get(position).getTitle();
        String memoContent = datas.get(position).getContent();
        holder.title.setText(memoTitle);
        holder.content.setText(memoContent);
        holder.content.setMinLines(MINLINES);
        holder.content.setMaxLines(MAXLINES);
        if(isImportant){
            holder.importance.setVisibility(View.VISIBLE);
        }
        if(isDeleted){
            holder.back.setVisibility(View.VISIBLE);
        }
        holder.copy.setOnClickListener(new CopyClickListener(holder.content));
        holder.delete.setOnClickListener(new DeleteClickListener(no,isDeleted));
        holder.back.setOnClickListener(new BackClickListener(no));
        holder.title.setOnClickListener(new ContentClickListener(no));
        holder.content.setOnClickListener(new ContentClickListener(no));
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout memoItemRoot;
        View importance;
        Button copy;
        Button delete;
        Button back;
        EditText title;
        TextView content;

        private ViewHolder(View itemView) {
            super(itemView);
            memoItemRoot = (LinearLayout) itemView.findViewById(R.id.memoItemRoot);
            importance = itemView.findViewById(R.id.memoItemImportance);
            copy = (Button) itemView.findViewById(R.id.memoItemCopy);
            delete = (Button) itemView.findViewById(R.id.memoItemDelete);
            back =(Button)itemView.findViewById(R.id.memoItemBack);
            title = (EditText) itemView.findViewById(R.id.memoItemTitle);
            content = (TextView) itemView.findViewById(R.id.memoItemContent);
        }
    }

//    private class ImportanceChangeListener implements CompoundButton.OnCheckedChangeListener{
//        long no;
//        String dirCode;
//        String content;
//
//        private ImportanceChangeListener(long no, String dirCode, String content) {
//            this.no = no;
//            this.dirCode = dirCode;
//            this.content = content;
//        }
//        @Override
//        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//            realmController.modifyMemo(no,b,dirCode,content);
//        }
//    }
    private class CopyClickListener implements View.OnClickListener{
        TextView content = null;
        String memoContent = null;

        public CopyClickListener(TextView content) {
            this.content = content;
            this.memoContent = content.getText().toString();
        }

        @Override
        public void onClick(View view) {
            CopyTool.copyMemo(content);
        }
    }


    private class DeleteClickListener implements View.OnClickListener{
        long no;
        boolean isDeleted;

        public DeleteClickListener(long no,boolean isDeleted) {
            this.no = no;
            this.isDeleted = isDeleted;
        }

        @Override
        public void onClick(View view) {
            if(isDeleted){
                AlertDialog deletDialog = new AlertDialog.Builder(context)
                        .setTitle("Delete Memo")
                        .setMessage("Memo will be deleted permanently")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                realmController.deleteMemoForever(no);
                            }
                        })
                        .setNeutralButton("Cancel",null)
                        .setCancelable(true)
                        .create();
                deletDialog.show();
            }else{
                Date nDate = Calendar.getInstance().getTime();
                realmController.deleteMemo(no,nDate);
                notifyItemRangeRemoved(0,1);
            }
        }
    }
    public static final String MEMO_NO = "Memo_NO";

    private class ContentClickListener implements View.OnClickListener{
        long no;

        public ContentClickListener(long no) {
            this.no = no;
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, MemoEditorViewImpl.class);
            intent.putExtra(MEMO_NO,no);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }
    private class BackClickListener implements View.OnClickListener{
        long no;

        public BackClickListener(long no) {
            this.no = no;
        }

        @Override
        public void onClick(View view) {
            realmController.recycleMemo(no);
        }
    }
}
