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
    public static byte[] in2Bytes(int num,int len){
        byte[] bytes = new byte[len];
        //从低位到高位填充字节数组
        //只考虑无符号，不考虑value负数
        for (int i = 0;i < len;i++){
            bytes[len-i-1] = (byte)((num>>(8*i)&0xff));
        }
        return bytes;
    }
}
