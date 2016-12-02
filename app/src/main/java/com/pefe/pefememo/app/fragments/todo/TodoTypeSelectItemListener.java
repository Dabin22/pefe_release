package com.pefe.pefememo.app.fragments.todo;

import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

/**
 * Created by Dabin on 2016-11-27.
 */

public class TodoTypeSelectItemListener implements AdapterView.OnItemSelectedListener {

    TodoHandler handler;
    public TodoTypeSelectItemListener(TodoHandler handler){
        this.handler = handler;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        TextView type_view = (TextView) view;
        String type = "";
        if(type_view!=null) {
            type = type_view.getText().toString();
        }
        if (!type.equals("")) {
            handler.setTodoType(type);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
