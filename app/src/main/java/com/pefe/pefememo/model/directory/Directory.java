package com.pefe.pefememo.model.directory;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;

/**
 * Created by dodoproject on 2016-11-07.
 */

public class Directory extends RealmObject {

    @Ignore
    public static final String PURPOSE_REGULAR ="Regular";
    @Ignore
    public static final String PURPOSE_TODO ="Todo";


    private int no;
    private String code;
    private String name;
    private String pw;


    public int getNo() {return no;}
    public void setNo(int no) { this.no = no;}
    public String getCode() {return code;}
    public void setCode(String code) {this.code = code;}
    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
    public String getPw() {return pw;}
    public void setPw(String pw) {this.pw = pw;}
}
