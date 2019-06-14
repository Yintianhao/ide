package com.mine.ide.util;

import com.mine.ide.test.ProjectTest;

import javax.tools.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
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
    /*正则表达式，表示class XXXXX*/
    private static Pattern CLASS_PATTERN = Pattern.compile("class\\s+([$_a-zA-Z][$_a-zA-Z0-9]*)\\s*");
    public static byte[] compile(String sourceCode,
                                 DiagnosticCollector<JavaFileObject> collector){
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();//获得javacompile实例
        JavaFileManager javaFileManager = new TmpJavaFileManager(
                compiler.getStandardFileManager(collector,null,null)
        );
        //根据pattern中的模式从字符串中匹配类名
        Matcher matcher = CLASS_PATTERN.matcher(sourceCode);
        String className;//类名
        if(matcher.find()){
            className  = matcher.group(1);
        }else {
            throw new IllegalArgumentException("No valid class");
        }
        //把源码字符串构造成JavaFileObject，提供给编译过程
        JavaFileObject sourceFileObject =
                new TmpJavaFileObject(className,sourceCode);
        //编译结果
        Boolean result = compiler.getTask(null,
                javaFileManager,
                collector,
                null,
                null,
                Arrays.asList(sourceFileObject)).call();
        JavaFileObject compileByteObject = fileObjectMap.get(className);
        if (result&&compileByteObject!=null)
            return ((TmpJavaFileObject)compileByteObject).getCompiledBytes();
        return null;
    }

    /**
     * 管理JavaFileObject的类*/
    public static class TmpJavaFileManager
            extends ForwardingJavaFileManager<JavaFileManager>{
        protected TmpJavaFileManager(JavaFileManager manager){
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
            JavaFileObject object =
                    new TmpJavaFileObject(className,kind);
            fileObjectMap.put(className,object);
            return object;
        }

    }
    /**
     * 封装表示源码和字节码的对象*/
    public static class TmpJavaFileObject
            extends SimpleJavaFileObject{
        private String sourceCode;
        private ByteArrayOutputStream outputStream;
        /**
         * 构造用来存储源码的JavaFileObject
         * 传入源代码source，调用父类的构造方法
         * 创建kind=Kind.SOURCE的JavaFileObject对象*/
        public TmpJavaFileObject(String name, String sourceCode){
            // 1、先初始化父类，由于该URI是通过类名来完成的，必须以.java结尾。
            // 2、如果是一个真实的路径，比如是file:///test/demo/Hello.java则不需要特别加.java
            // 3、这里加的String:///并不是一个真正的URL的schema, 只是为了区分来源
            super(URI.create("String:///"+name+Kind.SOURCE.extension),Kind.SOURCE);
            this.sourceCode = sourceCode;
        }
        /**
         * 构造用来存储字节码的JavaFileObject
         * 传入kind，即表示需要构造何种类型的JvaFileObject*/
        public TmpJavaFileObject(String name,Kind kind){
            super(URI.create("String:///"+name+Kind.SOURCE.extension),kind);
            this.sourceCode = null;
        }
        @Override
        public OutputStream openOutputStream()
                throws IOException{
            outputStream = new ByteArrayOutputStream();
            return outputStream;
        }
        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors){
            if(sourceCode == null){
                throw new IllegalArgumentException("source == null");
            }
            return sourceCode;
        }

        public byte[] getCompiledBytes(){
            return outputStream.toByteArray();
        }
    }
}
