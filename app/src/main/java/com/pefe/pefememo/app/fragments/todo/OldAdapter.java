package com.pefe.pefememo.app.fragments.todo;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.pefe.pefememo.R;
import com.pefe.pefememo.model.todo.Todo;

import java.util.ArrayList;

/**
 * Created by Dabin on 2016-11-28.
 */

public class OldAdapter extends RecyclerView.Adapter<OldAdapter.ViewHolder> {

    ArrayList<Todo> datas;
    LayoutInflater inflater;

    public OldAdapter(ArrayList<Todo> datas, LayoutInflater inflater){
        this.datas = datas;
        this.inflater = inflater;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_registered_todo_list,null);
        Log.e("view",view+"");
        return new ViewHolder(view);
    }

    private Todo todo;
    @Override
    public void onBindViewHolder(OldAdapter.ViewHolder holder, int position) {
        Log.e("oldAdapter","data = " + datas.get(position));
        todo = datas.get(position);
        holder.tv_unput_todo.setText(todo.getContent());
        holder.iv_unput_todo.setImageResource(TodoTypeImg.getTypeImgSrc(todo.getType()));
        holder.ck_todo_done.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    todo.setDone(true);
                }else{
                    todo.setDone(false);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public ArrayList<Todo> popData() {
        ArrayList<Todo> recycleDatas = new ArrayList<>();
        for (Todo todo : datas){
            if(todo.isDone()){
                todo.setDone(false);
                recycleDatas.add(todo);
            }
        }
        return recycleDatas;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_unput_todo;
        ImageView iv_unput_todo;
        CheckBox ck_todo_done;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_unput_todo = (TextView) itemView.findViewById(R.id.tv_register_todo);
            iv_unput_todo = (ImageView) itemView.findViewById(R.id.iv_register_todo);
            ck_todo_done = (CheckBox) itemView.findViewById(R.id.ck_todo_done);
        }
    }
}
