package com.pefe.pefememo.app.fragments.todo;

import com.pefe.pefememo.model.todo.Todo;

/**
 * Created by Dabin on 2016-11-27.
 */

public class TodoTypeImg {
    public static int getTypeImgSrc(String type)
    {
        int imgSrc=0;
        if(type.equals(Todo.REPEAT)){
            imgSrc =android.R.drawable.ic_menu_rotate;
        }else if(type.equals(Todo.ONCE)){
            imgSrc = android.R.drawable.ic_menu_info_details;
        }else if(type.equals(Todo.OLD)){
            imgSrc = android.R.drawable.ic_menu_agenda;
        }

        return imgSrc;
    }
}
