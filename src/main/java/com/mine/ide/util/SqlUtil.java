package com.mine.ide.util;

import com.sun.tools.internal.xjc.model.CReferencePropertyInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * @author yintianhao
 * @createTime 20190621 9:00
 * @description Sql语句分割
 */
public class SqlUtil {
    public static final String CREATE = "create";
    public static final String UPDATE = "update";
    public static final String SELECT = "select";
    public static final String DELETE = "delete";
    public static final String DROP = "drop";
    public static final String INSERT = "insert";
    public static final String SHOW = "show";
    public static final String USE = "use";
    public static HashMap<String,ArrayList<String>> split(String sql){

        HashMap<String,ArrayList<String>> res = new HashMap<>();
        String[] strs = sql.split(";");
        for (String str:strs){
            if (str.indexOf(CREATE)!=-1){
                if (res.containsKey(CREATE)){
                    res.get(CREATE).add(str);
                }else {
                    ArrayList<String> list = new ArrayList<>();
                    list.add(str);
                    res.put(CREATE,list);
                }
            } else if (str.indexOf(SELECT)!=-1){
                if (res.containsKey(SELECT)){
                    res.get(SELECT).add(str);
                }else {
                    ArrayList<String> list = new ArrayList<>();
                    list.add(str);
                    res.put(SELECT,list);
                }
            }else if (str.indexOf(DROP)!=-1){
                if (res.containsKey(DROP)){
                    res.get(DROP).add(str);
                }else {
                    ArrayList<String> list = new ArrayList<>();
                    list.add(str);
                    res.put(DROP,list);
                }
            }else if (str.indexOf(DELETE)!=-1){
                if (res.containsKey(DELETE)){
                    res.get(DELETE).add(str);
                }else{
                    ArrayList<String> list = new ArrayList<>();
                    list.add(str);
                    res.put(DELETE,list);
                }
            }else if (str.indexOf(UPDATE)!=-1){
                if (res.containsKey(UPDATE)){
                    res.get(UPDATE).add(str);
                }else {
                    ArrayList<String> list = new ArrayList<>();
                    list.add(str);
                    res.put(UPDATE,list);
                }
            }else if(str.indexOf(INSERT)!=-1){
                if (res.containsKey(INSERT)){
                    res.get(INSERT).add(str);
                }else {
                    ArrayList<String> list = new ArrayList<>();
                    list.add(str);
                    res.put(INSERT,list);
                }
            }else if (str.indexOf(SHOW)!=-1){
                if (res.containsKey(SHOW))
                    res.get(SHOW).add(str);
                else {
                    ArrayList<String> list = new ArrayList<>();
                    list.add(str);
                    res.put(SHOW,list);
                }
            }else if (str.indexOf(USE)!=-1){
                if (res.containsKey(USE))
                    res.get(USE).add(str);
                else{
                    ArrayList<String> list = new ArrayList<>();
                    list.add(str);
                    res.put(USE,list);
                }
            }
        }
        return res;
    }
}
