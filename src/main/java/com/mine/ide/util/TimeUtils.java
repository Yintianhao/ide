package com.mine.ide.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author yintianhao
 * @createTime 20190616 10:57
 * @description 时间工具
 */
public class TimeUtils {
    public static String getTimeInfo(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        return dateFormat.format(new Date());
    }
}
