package com.pefe.pefememo.app.fragments.todo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pefe.pefememo.R;
import com.pefe.pefememo.model.todo.Todo;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.Sort;

/**
 * Created by Dabin on 2016-11-27.
 */

public class UnRegisterAdapter extends RealmRecyclerViewAdapter<Todo, UnRegisterAdapter.ViewHolder> {

    private static final String ONCE = "ONCE_TODO";
    private static final String REPEAT = "REPEAT_TODO";
    private static final String OLD = "OLD_TODO";
    private Context context;
    private TodoDragListener dragListener;
    private TodoLongClickListener longClickListener;

    public UnRegisterAdapter(@NonNull Context context, @Nullable OrderedRealmCollection<Todo> data, boolean autoUpdate, TodoDragListener dragListener) {
        super(context, data, autoUpdate);
        this.context = context;
        //TODO Adapter에 NotifyItemChanged가 필요함 (또는 NotifyItemRemoved), 소스코드 http://stackoverflow.com/questions/28995380/best-practices-to-use-realm-with-a-recycler-view
        this.dragListener = dragListener;
        longClickListener = new TodoLongClickListener();
    }

    @Override
    public UnRegisterAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_unregister_todo_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UnRegisterAdapter.ViewHolder holder, int position) {
        Todo todo = getData().get(position);
        holder.tv_unRegister_todo.setText(todo.getContent());
        holder.iv_unRegister_todo.setImageResource(TodoTypeImg.getTypeImgSrc(todo.getType()));
        setViewTag(todo.getType(), holder.itemView, position);


    }

    private void setViewTag(String type, View itemView, int position) {
        TodoViewType viewType = new TodoViewType();
        if (type.equals(ONCE)) {
            viewType.setType(ONCE);
            viewType.setIndex(position);
            itemView.setTag(viewType);
            Log.e("unputAdapter", "once position =" + position);
        } else if (type.equals(REPEAT)) {
            viewType.setType(REPEAT);
            viewType.setIndex(position);
            itemView.setTag(viewType);
            Log.e("unputAdapter", "repeat position =" + position);
        } else if (type.equals(OLD)) {
            viewType.setType(OLD);
            viewType.setIndex(position);
            itemView.setTag(viewType);
            Log.e("unputAdapter", "old position =" + position);
        } else {
            Log.e("error", "non type!");
        }
    }

    @Override
    public int getItemCount() {
        return getData().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_unRegister_todo;
        ImageView iv_unRegister_todo;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_unRegister_todo = (TextView) itemView.findViewById(R.id.tv_unRegister_todo);
            iv_unRegister_todo = (ImageView) itemView.findViewById(R.id.iv_unRegister_todo);
            itemView.setOnDragListener(dragListener);
            itemView.setOnLongClickListener(longClickListener);
        }
    }
}
