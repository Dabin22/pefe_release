package com.pefe.pefememo.model.memo;

import io.realm.RealmObject;

/**
 * Created by dodoproject on 2016-11-07.
 */

public class Memo extends RealmObject {
    private long no;
    private String dirCode;
    private boolean important;
    private String content;

    public long getNo() { return no;}
    public void setNo(long no) {this.no = no;}
    public String getDirCode() {return dirCode;}
    public void setDirCode(String dirCode) {this.dirCode = dirCode;}
    public boolean isImportant() {return important;}
    public void setImportant(boolean important) {this.important = important;}
    public String getContent() {return content;}
    public void setContent(String content) {this.content = content;}
}
