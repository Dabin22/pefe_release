package com.pefe.pefememo.model.memo;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by dodoproject on 2016-11-07.
 */

public class Memo extends RealmObject {

    @PrimaryKey
    private long no;

    private String dirCode;
    private boolean important;
    private String content;
    private Date createDate;

    public long getNo() { return no;}
    public void setNo(long no) {this.no = no;}
    public String getDirCode() {return dirCode;}
    public void setDirCode(String dirCode) {this.dirCode = dirCode;}
    public boolean isImportant() {return important;}
    public void setImportant(boolean important) {this.important = important;}
    public String getContent() {return content;}
    public void setContent(String content) {this.content = content;}
    public Date getCreateDate() {return createDate;}
    public void setCreateDate(Date createDate) {this.createDate = createDate;}
}
