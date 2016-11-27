package com.pefe.pefememo.app.fragments.todo;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pefe.pefememo.R;
import com.pefe.pefememo.model.todo.Todo;

import java.util.ArrayList;
import java.util.Collections;

import static com.pefe.pefememo.R.id.iv_unput_todo;
import static com.pefe.pefememo.R.id.tv_unput_todo;

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

    public void addData(Todo todo){
        datas.add(todo);
        recycle();
    }
    public void addDatas(ArrayList<Todo> subDatas){
        datas.addAll(subDatas);
        recycle();
    }
    public void removeData(Todo todo){
        datas.remove(todo);
        recycle();
    }
    public void removeData(int pickedIndex) {
        datas.remove(pickedIndex);
        recycle();
    }
    public void swapPosition(int tar, int src){
        Collections.swap(datas,tar,src);
        recycle();
    }
    public Todo pop(int pickedIndex) {
        Todo pop_todo =datas.get(pickedIndex);
        removeData(pop_todo);
        recycle();
        return pop_todo;
    }

    private void recycle(){
        notifyDataSetChanged();
    }


    @Override
    public void onBindViewHolder(UnRegisterAdapter.ViewHolder holder, int position) {
        Todo todo = datas.get(position);
        holder.tv_unRegister_todo.setText(todo.getContent());
        holder.iv_unRegister_todo.setImageResource(TodoTypeImg.getTypeImgSrc(todo.getType()));
        setViewTag(todo.getType(),holder.itemView,position);

    }

    private void setViewTag(String type, View itemView, int position) {
        TodoViewType viewType = new TodoViewType();
        if(type.equals("Once")){
            viewType.setType("Once");
            viewType.setIndex(position);
            itemView.setTag(viewType);
            Log.e("unputAdapter","once position =" + position);
        }else if(type.equals("Repeat")){
            viewType.setType("Repeat");
            viewType.setIndex(position);
            itemView.setTag(viewType);
            Log.e("unputAdapter","repeat position =" + position);
        }else if(type.equals("Old")){
            viewType.setType("Old");
            viewType.setIndex(position);
            itemView.setTag(viewType);
            Log.e("unputAdapter","old position =" + position);
        }else{
            Log.e("error","non type!");
        }
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_unRegister_todo;
        ImageView iv_unRegister_todo;
        public ViewHolder(View itemView) {
            super(itemView);
            tv_unRegister_todo =(TextView)itemView.findViewById(tv_unput_todo);
            iv_unRegister_todo = (ImageView)itemView.findViewById(iv_unput_todo);
        }
    }
}
