package com.pefe.pefememo.model.todo;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by dodoproject on 2016-11-07.
 */


public class Todo extends RealmObject {


    private long no;
    private boolean repeated;
    private Put put;
    private boolean done;
    private String content;
    private Date CreatDate;

    public long getNo() {return no;}
    public void setNo(long no) {this.no = no;}
    public boolean isRepeated() {return repeated;}
    public void setRepeated(boolean repeated) {this.repeated = repeated;}
    public Put getPut() {return put;}
    public void setPut(Put put) {this.put = put;}
    public boolean isDone() {return done;}
    public void setDone(boolean done) {this.done = done;}
    public String getContent() {return content;}
    public void setContent(String content) {this.content = content;}
    public Date getCreatDate() {return CreatDate;}
    public void setCreatDate(Date creatDate) {CreatDate = creatDate;}

}

