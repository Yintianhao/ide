package com.mine.ide.entity;

/**
 * @author yintianhao
 * @createTime 20190617 14:55
 * @description 用户
 */
public class User {
    String userid;
    String password;

    public User(String userid, String password) {
        this.userid = userid;
        this.password = password;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
