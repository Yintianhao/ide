package com.mine.ide.util;

/**
 * @author yintianhao
 * @createTime 20190605 16:04
 * @description 类加载器
 */
public class HotSwapClassLoader extends ClassLoader{
    public HotSwapClassLoader(){
        super(HotSwapClassLoader.class.getClassLoader());
    }
    public Class loadClass(byte[] bytes){
        return defineClass(null,bytes,0,bytes.length);
    }
}
