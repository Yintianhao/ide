package com.mine.ide.util;

import javax.tools.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * @author yintianhao
 * @createTime 20190424 14:40
 * @description 编译器
 */
public class Compiler {
    //存放JavaFileObject
    private static Map<String,JavaFileObject> fileObjectMap
            = new ConcurrentHashMap<>();
    /* 使用 Pattern 预编译*/
    private static Pattern CLASS_PATH = Pattern.compile("class\\s+([$_a-zA-Z][$_a-zA-Z0-9]*)\\s*");
    public static byte[] compile(String sourceCode,
                                 DiagnosticCollector<JavaFileObject> collector){
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        JavaFileManager javaFileManager;
        return new byte[]{};//挖坑待填
    }
    /**
     * 管理JavaFileObject的类*/
    public static class MyJavaFileManager
            extends ForwardingJavaFileManager<JavaFileManager>{
        protected MyJavaFileManager(JavaFileManager manager){
            super(manager);
        }
        @Override
        public JavaFileObject getJavaFileForInput(
                JavaFileManager.Location location, String className,
                JavaFileObject.Kind kind
        )throws IOException {
            JavaFileObject javaFileObject = fileObjectMap.get(className);
            if (javaFileObject==null){
                return super.getJavaFileForInput(location,className,kind);
            }
            return javaFileObject;
        }
        @Override
        public JavaFileObject getJavaFileForOutput(
                JavaFileManager.Location location,String className,
                JavaFileObject.Kind kind,FileObject sibling
        )throws IOException{
            return new MyJavaFileObject("挖坑","待填");
        }
        /**
         * 封装表示源码和字节码的对象*/
        public static class MyJavaFileObject
                extends SimpleJavaFileObject{
            private String sourceCode;
            private ByteArrayOutputStream outputStream;
            /**
             * 构造用来存储源码的JavaFileObject
             * 传入源代码source，调用父类的构造方法
             * 创建kind=Kind.SOURCE的JavaFileObject*/
            public MyJavaFileObject(String name,String sourceCode){
                super(URI.create("String:///"+name+Kind.SOURCE.extension),Kind.SOURCE);
                this.sourceCode = sourceCode;
            }
        }
    }
}
