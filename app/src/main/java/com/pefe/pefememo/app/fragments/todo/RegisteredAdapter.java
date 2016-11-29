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
import com.pefe.pefememo.model.todo.SelectedTodo;

import java.util.ArrayList;

/**
 * Created by Dabin on 2016-11-27.
 */

public class RegisteredAdapter extends RecyclerView.Adapter<RegisteredAdapter.ViewHolder> {

    private ArrayList<SelectedTodo> datas;
    private SelectedTodo todo;
    private int belong_day = -1;
    private TodoDragListener dragListener;
    private TodoLongClickListener longClickListener;
    private TodoHandler handler;


    public RegisteredAdapter(TodoDragListener dragListener, TodoHandler handler) {
        datas = new ArrayList<>();
        this.dragListener = dragListener;
        longClickListener = new TodoLongClickListener();
        this.handler = handler;
    }

    @Override
    public RegisteredAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_registered_todo_list, parent, false);
        return new ViewHolder(view);
    }

    public void addData(SelectedTodo todo) {
        datas.add(todo);
        recycle();
    }

    public void addDatas(ArrayList<SelectedTodo> subDatas) {
        datas.addAll(subDatas);
        recycle();
    }

    public void removeData(SelectedTodo todo) {
        datas.remove(todo);
        recycle();
    }

    public void removeData(int pickedIndex) {
        datas.remove(pickedIndex);
        recycle();
    }

    public SelectedTodo pop(int pickedIndex) {
        SelectedTodo pop_todo = datas.get(pickedIndex);
        removeData(pop_todo);
        recycle();
        return pop_todo;
    }

    public String getType(int index) {
        return datas.get(index).getType();
    }

    private void recycle() {
        notifyDataSetChanged();
    }


    @Override
    public void onBindViewHolder(RegisteredAdapter.ViewHolder holder, int position) {
        todo = datas.get(position);
        holder.tv_unput_todo.setText(todo.getContent());
        holder.iv_unput_todo.setImageResource(TodoTypeImg.getTypeImgSrc(todo.getType()));
        if (!handler.compare_date(todo.getBelongDate()).equals("past")) {
            holder.ck_todo_done.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean done) {
                    todo.setDone(done);
                }
            });
        }else{
            holder.ck_todo_done.setVisibility(View.GONE);
        }
        TodoViewType viewType = new TodoViewType();
        if (belong_day == 0) {
            viewType.setType("Today");
            viewType.setIndex(position);
            viewType.setBelongDate(todo.getBelongDate());
            holder.itemView.setTag(viewType);
        } else if (belong_day == -1) {
            viewType.setType("Tommorow");
            viewType.setIndex(position);
            holder.itemView.setTag(viewType);
        } else {
            Log.e("error", "belong_day is null!");
        }
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public boolean isExistence(SelectedTodo sTodo) {
        for(SelectedTodo todo : datas){
            if(todo.getContent().equals(sTodo.getContent()))
                return true;
        }
        return false;
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
            itemView.setOnLongClickListener(longClickListener);
            itemView.setOnDragListener(dragListener);
        }
    }
}
