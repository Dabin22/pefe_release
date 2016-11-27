package com.pefe.pefememo.model.todo;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by dodoproject on 2016-11-07.
 */


public class Todo extends RealmObject {


    private long no;
    private String type;
    private String content;
    private Date CreatDate;
    private boolean done;

    public boolean isDone() { return done; }

    public void setDone(boolean done) { this.done = done; }

    public long getNo() {
        return no;
    }

    public void setNo(long no) {
        this.no = no;
    }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreatDate() {
        return CreatDate;
    }

    public void setCreatDate(Date creatDate) {
        CreatDate = creatDate;
    }

}

