package com.pefe.pefememo.model.directory;

import android.support.annotation.NonNull;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Created by dodoproject on 2016-11-07.
 */

public class Directory extends RealmObject {


    private long no;
    private long order;

    @PrimaryKey
    private String code;

    private String name;
    private String pw;


    public long getNo() {return no;}
    public void setNo(long no) { this.no = no;}
    public String getCode() {return code;}
    public void setCode(String code) {this.code = code;}
    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
    public String getPw() {return pw;}
    public void setPw(String pw) {this.pw = pw;}
    public long getOrder() {return order;}
    public void setOrder(long order) {this.order = order;}
}
