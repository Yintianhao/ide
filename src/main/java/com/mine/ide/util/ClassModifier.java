package com.mine.ide.util;

/**
 * @author yintianhao
 * @createTime 20190515 17:50
 * @description Class修改器
 */
public class ClassModifier {
    //Class文件中常量池的起始偏移
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
    /**
     * 从0x00000008开始先后取两个字节，表示常量池中的常量的个数
     * @return 常量池中常量的个数
     * */
    public int getConstantPoolCount(){
        //版本号后两个字节
        return ByteUtil.byteToInt(classByte,CONSTANT_POOL_COUNT_INDEX,u2);
    }
    /**
     * 字节码修改器 替换字节码常量池的oldStr为newStr
     * @param oldStr
     * @param newStr
     * @return 修改后的字节码字节数组
     * */
    public byte[] modifyUTF8Constant(String oldStr,String newStr){
        int cpc = getConstantPoolCount();//获取常量池的元素个数
        int offSet = CONSTANT_POOL_COUNT_INDEX + u2;//真正的常量的起始地址,常量池之后的字节
        for(int i = 0;i < cpc;i++){
            int tag = ByteUtil.byteToInt(classByte,offSet,u1);//取真实起始地址后一个字节并转化为int
            if(tag==CONSTANT_UTF8_INFO){
                //CONSTANT_UTF_INFO的结构，第一项一个字节，即+u1，而长度是2
                int len = ByteUtil.byteToInt(classByte,offSet+u1,u2);
                offSet = offSet+u1+u2;//偏移值加u1+u2
                String str = ByteUtil.byteToString(classByte,offSet,len);
                if (str.equals(oldStr)){
                    byte[] strReplaceBytes = ByteUtil.stringToBytes(newStr);
                    byte[] intReplaceBytes = ByteUtil.inToBytes(strReplaceBytes.length,u2);
                    //替换成新的字符串的长度
                    //byte[] oldBytes,int offset,int len,byte[] replaceBytes
                    classByte = ByteUtil.byteReplace(classByte,offSet-u2,u2,intReplaceBytes);
                    //替换字符本身
                    classByte = ByteUtil.byteReplace(classByte,offSet,len,strReplaceBytes);
                }else {
                    offSet = offSet + len;
                }
            }else {
                //如果不是CONSTANT_UTF8_INFO类型
                offSet = offSet + CONSTANT_ITEM_LENGTH[tag];
            }
        }
        return classByte;
    }
}
