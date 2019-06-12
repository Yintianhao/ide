package com.mine.ide.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
        ClassModifier modifier = new ClassModifier(classByte);
        //调用ClassModifier的方法进行修改
        byte[] modifiedByte = modifier.modifyUTF8Constant("java/lang/System","com/mine/ide/util/HackSystem");
        HotSwapClassLoader classLoader = new HotSwapClassLoader();
        Class classes = classLoader.loadClass(modifiedByte);
        try {
            Method mainMethod = classes.getMethod("main",new Class[]{String[].class});
            mainMethod.invoke(null,new String[]{null});
        }catch (NoSuchMethodException e){
            e.printStackTrace();
        }catch (IllegalAccessException e){
            e.printStackTrace();
        }catch (InvocationTargetException e) {
            //将异常信息返回给前端
            //System.out.println("Error");
            e.getCause().printStackTrace(HackSystem.err);
        }
        String back = HackSystem.getBufferString();
        HackSystem.closeBuffer();
        return back;
    }
}
