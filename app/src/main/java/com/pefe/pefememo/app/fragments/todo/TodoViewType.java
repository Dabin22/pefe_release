package com.pefe.pefememo.app.fragments.todo;

import java.util.Date;

/**
 * Created by Dabin on 2016-11-27.
 */

public class TodoViewType {
    private String type;
    private int index;
    private Date belongDate = null;

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }

    public int getIndex() { return index; }

    public void setIndex(int index) { this.index = index; }

    public Date getBelongDate() { return belongDate; }

    public void setBelongDate(Date belongDate) { this.belongDate = belongDate; }
}
