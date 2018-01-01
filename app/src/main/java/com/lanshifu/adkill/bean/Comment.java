package com.lanshifu.adkill.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by lanshifu on 2017/12/23.
 */

public class Comment extends BmobObject {

    private String userName;
    private String title;
    private String content;
    private String status;
    private Number mark;

    public Number getMark() {
        return mark;
    }

    public void setMark(Number mark) {
        this.mark = mark;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
