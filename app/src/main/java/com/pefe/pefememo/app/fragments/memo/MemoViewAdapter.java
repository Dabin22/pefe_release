package com.pefe.pefememo.app.fragments.memo;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.jakewharton.rxbinding.widget.TextViewTextChangeEvent;
import com.pefe.pefememo.R;
import com.pefe.pefememo.Realm.RealmControl;
import com.pefe.pefememo.model.memo.Memo;

import java.util.ArrayList;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;
import rx.Observable;
import rx.Subscriber;

import static com.jakewharton.rxbinding.widget.RxTextView.textChangeEvents;

/**
 * Created by dodoproject on 2016-11-25.
 */

public class MemoViewAdapter extends RealmRecyclerViewAdapter<Memo,MemoViewAdapter.ViewHolder> {
    Context context;
    RealmControl realmControl = null;

    private final int MAXLINES =3;
    private final float TEMP_ALPHA = 0.3f;

    public MemoViewAdapter(@NonNull Context context, @Nullable OrderedRealmCollection<Memo> data,
                           boolean autoUpdate, Context context1, RealmControl realmControl, ArrayList<Memo> memos) {
        super(context, data, autoUpdate);
        this.context = context1;
        this.realmControl = realmControl;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View memo = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_memo,parent,false);
        return new ViewHolder(memo);
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        long no = getData().get(position).getNo();
        boolean isImportant = getData().get(position).isImportant();
        String dirCode = getData().get(position).getDirCode();
        String memoContent = getData().get(position).getContent();
        holder.importance.setChecked(isImportant);
        holder.importance.setOnCheckedChangeListener(new ImportanceChangeListener(no, dirCode, holder.content));
        holder.copy.setOnClickListener(new CopyClickListener(holder.content));
        holder.delete.setOnClickListener(new DeleteClickListener(no));
        holder.content.setText(memoContent);
        holder.content.setMaxLines(MAXLINES);
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout memoItemRoot;
        ToggleButton importance;
        Button copy;
        Button delete;
        EditText content;
        private ViewHolder(View itemView) {
            super(itemView);
            memoItemRoot = (LinearLayout) itemView.findViewById(R.id.memoItemRoot);
            importance = (ToggleButton) itemView.findViewById(R.id.memoItemImportance);
            copy = (Button) itemView.findViewById(R.id.memoItemCopy);
            delete = (Button) itemView.findViewById(R.id.memoItemDelete);
            content = (EditText) itemView.findViewById(R.id.memoItemContent);
        }
    }

    private class ImportanceChangeListener implements CompoundButton.OnCheckedChangeListener{
        long no;
        String dirCode;
        EditText content;

        private ImportanceChangeListener(long no, String dirCode, EditText content) {
            this.no = no;
            this.dirCode = dirCode;
            this.content = content;
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            String memoContent = content.getText().toString();
            realmControl.modifyMemo(no,b,dirCode,memoContent);
        }
    }
    private class CopyClickListener implements View.OnClickListener{
        EditText content = null;
        String memoContent = null;

        public CopyClickListener(EditText content) {
            this.content = content;
            this.memoContent = content.getText().toString();
        }

        @Override
        public void onClick(View view) {
            copyMemo(memoContent);
        }
    }

    private final String COPYCLIP_LABEL = "COPIED_MEMO";
    private void copyMemo(String content ){
        ClipboardManager clipboardManager = (ClipboardManager)context.getSystemService(context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText(COPYCLIP_LABEL, content);
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(context,"memo copied", Toast.LENGTH_SHORT).show();
    }


    private class DeleteClickListener implements View.OnClickListener{
        long no;

        public DeleteClickListener(long no) {
            this.no = no;
        }

        @Override
        public void onClick(View view) {
            realmControl.deleteMemo(no);
        }
    }

}
