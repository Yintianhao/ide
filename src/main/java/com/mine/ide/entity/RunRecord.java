package com.mine.ide.entity;

import java.io.Serializable;

/**
 * @author yintianhao
 * @createTime 20190616 10:53
 * @description 运行记录
 */
public class RunRecord implements Serializable {
    private String userid;//用户ID
    private String code;//代码
    private String timedetail;//时间详细信息
    private String language;//语言类型
    public RunRecord(String userid, String code, String timedetail, String language) {
        this.userid = userid;
        this.code = code;
        this.timedetail = timedetail;
        this.language = language;
    }
    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setTimedetail(String timedetail) {
        this.timedetail = timedetail;
    }

    public String getCode() {
        return code;
    }

    public String getTimedetail() {
        return timedetail;
    }
}
