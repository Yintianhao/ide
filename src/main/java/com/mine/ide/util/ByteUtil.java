package com.mine.ide.util;

/**
 * @author yintianhao
 * @createTime 2019515 18:10
 * @description 字节工具
 */
public class ByteUtil {
    public static int byteToInt(byte[] bytes,int start,int len){
        int res = 0;
        int end = start+len;
        for(int i = start;i<end;i++){
            //byte-->unsigned int
            int cur = ((int)bytes[i])&0xff;
            //高位字节左移，不考虑value为负
            cur<<=(--len)*8;
            res += cur;
        }
        return res;
    }
    public static byte[] inToBytes(int num,int len){
        byte[] bytes = new byte[len];
        //从低位到高位填充字节数组
        //只考虑无符号，不考虑value负数
        for (int i = 0;i < len;i++){
            bytes[len-i-1] = (byte)((num>>(8*i))&0xff);
        }
        return bytes;
    }
    public static String byteToString(byte[] b,int start,int end){
        return new String(b,start,end);
    }
    public static byte[] stringToBytes(String str){
        return str.getBytes();
    }
    public static byte[] byteReplace(byte[] oldBytes,int offset,int len,byte[] replaceBytes){
        byte[] newBytes = new byte[oldBytes.length + replaceBytes.length - len];
        System.arraycopy(oldBytes, 0, newBytes, 0, offset);
        System.arraycopy(replaceBytes, 0, newBytes, offset, replaceBytes.length);
        System.arraycopy(oldBytes, offset + len, newBytes, offset + replaceBytes.length,
                oldBytes.length - offset - len);
        return newBytes;
    }
}
