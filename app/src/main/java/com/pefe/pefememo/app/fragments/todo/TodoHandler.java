package com.pefe.pefememo.app.fragments.todo;

import java.util.Date;

/**
 * Created by Dabin on 2016-11-27.
 */

public interface TodoHandler {
    void setTodoType(String type);

    void moveDate(int bottonID);

    void swapPosition(int src, int tar, String adapterType);

    void register_todo(String pickedType, int pickedIndex, String targetType);

    void stop();

    void setSpinner(int index);

    void changeBelongDate(Date pickedBelongDate, int pickedIndex);

    void unRegister_mode(Date pickedBelongDate, int pickedIndex);

    void change_mode(boolean isDlete);

    void isEntered(boolean check);

    void delete(String pickedType, int pickedIndex, Date pickedBelongDate);

    String compare_date(Date belongDate);
}
