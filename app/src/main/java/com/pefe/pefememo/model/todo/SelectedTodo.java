package com.pefe.pefememo.model.todo;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Dabin on 2016-11-27.
 */

public class SelectedTodo extends RealmObject {
    @PrimaryKey
    private long no;
    private boolean done;
    private String type;
    private String content;
    private Date belongDate;
    private Date putDate;


    public boolean isDone() { return done; }
    public void setDone(boolean done) { this.done = done; }

    public long getNo() {
        return no;
    }

    public void setNo(long no) {
        this.no = no;
    }

    public String getContent() {
        return content;
    }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getBelongDate() {
        return belongDate;
    }

    public void setBelongDate(Date belongDate) {
        this.belongDate = belongDate;
    }

    public Date getPutDate() {
        return putDate;
    }

    public void setPutDate(Date putDate) { this.putDate = putDate; }
}
