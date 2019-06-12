package com.mine.ide.util;

import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.channels.Channel;

/**
 * @author yintianhao
 * @createTime 20190521 23:23
 * @description 封装一个类
 */
public final class HackSystem {
    private HackSystem(){}
    public final static InputStream in = System.in;
    public final static PrintStream out = new HackPrintStream();
    public final static PrintStream err = out;
    /**
     * 获取当前线程的输出流中的内容
     * */
    public static String getBufferString(){
        return out.toString();
    }
    /**
     * 关闭当前线程的输出流
     * */
    public static void closeBuffer(){
        out.close();
    }
    private static volatile SecurityManager security = null;
    public static void setIn(InputStream in) {
        throw new SecurityException("Use hazardous method: System.setIn().");
    }

    public static void setOut(PrintStream out) {
        throw new SecurityException("Use hazardous method: System.setOut().");
    }

    public static void setErr(PrintStream err) {
        throw new SecurityException("Use hazardous method: System.setErr().");
    }
    public static Console console(){
        throw new SecurityException("User hazardous method: System.console()");
    }
    public static Channel inheritedChannel()throws IOException{
        throw new SecurityException("User hazardous method：System.inheritedChannel()");
    }
}
