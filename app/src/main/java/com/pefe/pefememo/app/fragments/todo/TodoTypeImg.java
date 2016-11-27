package com.pefe.pefememo.app.fragments.todo;

/**
 * Created by Dabin on 2016-11-27.
 */

public class TodoTypeImg {
    public static int getTypeImgSrc(String type)
    {
        int imgSrc=0;
        if(type.equals("Repeat")){
            imgSrc =android.R.drawable.ic_menu_rotate;
        }else if(type.equals("Once")){
            imgSrc = android.R.drawable.ic_menu_info_details;
        }else if(type.equals("Old")){
            imgSrc = android.R.drawable.ic_menu_agenda;
        }

        return imgSrc;
    }
}
