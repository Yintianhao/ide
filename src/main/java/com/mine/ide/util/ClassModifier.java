package com.mine.ide.util;

/**
 * @author yintianhao
 * @createTime 20190515 17:50
 * @description Class修改器
 */
public class ClassModifier {
    //Class文件中常量池的其实偏移
    private static final int CONSTANT_POOL_COUNT_INDEX = 8;
    //CONSTANT_UTF8_INFO的常量的tag
    private static final int CONSTANT_UTF8_INFO = 1;
    //常量池中十一种常量的长度集合 [tag]代表长度
    private static final int[] CONSTANT_ITEM_LENGTH = {-1,-1,-1,5,5,9,9,3,3,5,5,5,5};
    /**
     * 一个字节和两个字节的符号数，用在ClassByte数组中取tag和len
     * tag用u1个字节表示
     * len用u2个字节表示
     * */
    private static final int u1 = 1;
    private static final int u2 = 2;
    //修改的数组
    private byte[] classByte;
    public ClassModifier(byte[] classByte){
        this.classByte = classByte;
    }

}
