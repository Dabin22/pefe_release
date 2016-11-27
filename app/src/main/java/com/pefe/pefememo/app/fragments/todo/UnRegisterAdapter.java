package com.pefe.pefememo.app.fragments.todo;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pefe.pefememo.R;
import com.pefe.pefememo.model.todo.Todo;

import java.util.ArrayList;

/**
 * Created by Dabin on 2016-11-27.
 */

public class UnRegisterAdapter extends RecyclerView.Adapter<UnRegisterAdapter.ViewHolder> {

    private ArrayList<Todo> datas;
    //private Todo_handler handler;

    public UnRegisterAdapter(){
        datas = new ArrayList<>();
    }
    @Override
    public UnRegisterAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_unregister_todo_list,parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UnRegisterAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_unput_todo;
        ImageView iv_unput_todo;
        public ViewHolder(View itemView) {
            super(itemView);
            tv_unput_todo =(TextView)itemView.findViewById(R.id.tv_unput_todo);
            iv_unput_todo = (ImageView)itemView.findViewById(R.id.iv_unput_todo);
        }
    }
}
