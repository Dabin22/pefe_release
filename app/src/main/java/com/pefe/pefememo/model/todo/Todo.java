package com.pefe.pefememo.model.todo;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Created by dodoproject on 2016-11-07.
 */


public class Todo extends RealmObject {

    @Ignore
    public static final String ONCE = "ONCE_TODO";
    @Ignore
    public static final String REPEAT = "REPEAT_TODO";
    @Ignore
    public static final String OLD = "OLD_TODO";

    @PrimaryKey
    private long no;
    private String type;
    private String content;
    private Date createDate;
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

    public Date getCreateDate() {return createDate;}

    public void setCreateDate(Date createDate) {this.createDate = createDate;}
}

