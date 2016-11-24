package com.pefe.pefememo.model.todo;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by dodoproject on 2016-11-08.
 */


public class Put extends RealmObject {


    private boolean putIntoScheduler;
    private Date putDate;
    private Date belongDate;


    public boolean isPutIntoScheduler() {return putIntoScheduler;}
    public void setPutIntoScheduler(boolean putIntoScheduler) {this.putIntoScheduler = putIntoScheduler;}
    public Date getPutDate() {
        return putDate;
    }
    public void setPutDate(Date putDate) {
        this.putDate = putDate;
    }
    public Date getBelongDate() {return belongDate;}
    public void setBelongDate(Date belongDate) {this.belongDate = belongDate;}

}
