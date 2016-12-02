package com.pefe.pefememo.app.fragments.todo;

import com.pefe.pefememo.R;
import com.pefe.pefememo.model.todo.Todo;

/**
 * Created by Dabin on 2016-11-27.
 */

public class TodoTypeImg {
    public static int getTypeImgSrc(String type)
    {
        int imgSrc=0;
        if(type.equals(Todo.REPEAT)){
            imgSrc = R.drawable.ic_loop_whiteblue_36dp;
        }else if(type.equals(Todo.ONCE)){
            imgSrc = R.drawable.ic_exposure_plus_1_whiteblue_36dp;
        }else if(type.equals(Todo.OLD)){
            imgSrc = R.drawable.ic_access_time_whiteblue_36dp;
        }

        return imgSrc;
    }
}
