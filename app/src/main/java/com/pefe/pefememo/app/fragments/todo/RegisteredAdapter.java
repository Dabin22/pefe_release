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
import com.pefe.pefememo.model.todo.SelectedTodo;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

/**
 * Created by Dabin on 2016-11-27.
 */

public class RegisteredAdapter extends RealmRecyclerViewAdapter<SelectedTodo, RegisteredAdapter.ViewHolder> {


//    private ArrayList<SelectedTodo> datas;
    private SelectedTodo todo;
    private int belong_day = -1;
    private TodoDragListener dragListener;
    private TodoLongClickListener longClickListener;
    private Context context;
    private Calendar cal;
    private Date today;

    public RegisteredAdapter(@NonNull Context context, @Nullable OrderedRealmCollection<SelectedTodo> data, boolean autoUpdate,TodoDragListener dragListener) {
        super(context, data, autoUpdate);
        this.context =context;
        this.dragListener = dragListener;
        longClickListener = new TodoLongClickListener();
        today = modifi_customDate();
    }


    @Override
    public RegisteredAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context,R.layout.item_registered_todo_list, parent);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RegisteredAdapter.ViewHolder holder, int position) {
        todo = getData().get(position);
        holder.tv_unput_todo.setText(todo.getContent());
        holder.iv_unput_todo.setImageResource(TodoTypeImg.getTypeImgSrc(todo.getType()));
        if (!compare_date(todo.getBelongDate()).equals("past")) {
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

    public String compare_date(Date belongDate) {
        if (today.compareTo(belongDate) == 0) {
            return "same";
        } else if (today.compareTo(belongDate) > 0) {
            return "past";
        } else {
            return "future";
        }
    }

    //시,분,초를 모두 0으로 초기화 해주는 함수입니다.
    private Date modifi_customDate() {
        cal = Calendar.getInstance();
        SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-d");
        ParsePosition pos = new ParsePosition(0);
        return date_format.parse(date_format.format(cal.getTime()), pos);
    }

    @Override
    public int getItemCount() {
        return getData().size();
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
