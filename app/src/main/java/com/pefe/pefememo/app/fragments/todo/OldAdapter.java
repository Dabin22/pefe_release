package com.pefe.pefememo.app.fragments.todo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.pefe.pefememo.R;
import com.pefe.pefememo.model.todo.Todo;

import java.util.ArrayList;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

/**
 * Created by Dabin on 2016-11-28.
 */

public class OldAdapter extends RealmRecyclerViewAdapter<Todo,OldAdapter.ViewHolder> {

    Context context;
    ArrayList<Todo> remove_datas;

    public OldAdapter(@NonNull Context context, @Nullable OrderedRealmCollection<Todo> data, boolean autoUpdate) {
        super(context, data, autoUpdate);
        this.context = context;
        remove_datas = new ArrayList<>();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context,R.layout.item_registered_todo_list,parent);
        Log.e("view",view+"");
        return new ViewHolder(view);
    }

    private Todo todo;
    @Override
    public void onBindViewHolder(OldAdapter.ViewHolder holder, int position) {
        Log.e("oldAdapter","data = " + getData().get(position));
        todo = getData().get(position);
        holder.tv_unput_todo.setText(todo.getContent());
        holder.iv_unput_todo.setImageResource(TodoTypeImg.getTypeImgSrc(todo.getType()));
        holder.ck_todo_done.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    stack_remove_datas(todo);
                }else{
                    clear_remove_datas(todo);
                }
            }
        });

    }
    private void stack_remove_datas(Todo todo){
        remove_datas.add(todo);
    }

    private void clear_remove_datas(Todo todo){
        remove_datas.remove(todo);
    }

    public ArrayList<Todo> pop_remove_Datas(){
        return remove_datas;
    }

    @Override
    public int getItemCount() {
        return getData().size();
    }

    public ArrayList<Todo> popData() {
        ArrayList<Todo> recycleDatas = new ArrayList<>();
        for (Todo todo : getData()){
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
