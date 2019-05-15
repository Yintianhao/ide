package com.mine.ide.util;

/**
 * @author yintianhao
 * @createTime 2019515 17:30
 * @description 运行器
 * 执行过程：
 * 1 清空HackSystem中的缓存
 * 2 新建一个ClassModifier对象，并传入需要被修改的数组
 * 3 调用ClassModifier#modifyUTF8Constant修改：java/lang/System ->com/jvm/remoteinvoke/HackSystem
 * 4 新建一个类加载器，把字节数组加载为Class对象
 * 5 通过反射调用Class对象的main方法
 * 6 从HackSystem中获取返回结果
 */
public class JavaClassExecutor {
    public static String execute(byte[] classByte){
        return "";
    }
}
