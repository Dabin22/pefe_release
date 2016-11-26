package com.pefe.pefememo.app.fragments.memo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.pefe.pefememo.R;
import com.pefe.pefememo.model.directory.Directory;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

/**
 * Created by dodoproject on 2016-11-25.
 */

public class DirViewAdapter extends RealmRecyclerViewAdapter<Directory,DirViewAdapter.ViewHolder> {


    public DirViewAdapter(@NonNull Context context, @Nullable OrderedRealmCollection<Directory> data, boolean autoUpdate) {
        super(context, data, autoUpdate);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //View dir = View.inflate(parent.getContext(),R.layout.item_directory,null);
        View dir = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_directory,parent,false);
        return new ViewHolder(dir);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        String name = getData().get(position).getName();
        String dirCode = getData().get(position).getCode();
        String dirPw = getData().get(position).getPw();

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

        private dirClickListener(String code) {

            this.code = code;
        }

        @Override
        public void onClick(View view) {

        }
    }

}
