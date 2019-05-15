package com.mine.ide.service;

import com.mine.ide.util.Compiler;
import org.springframework.stereotype.Service;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * @author yintianhao
 * @createTime 20190424 14:19
 * @description 执行代码服务
 */
@Service
public class ExecuteService {
    /*运行时间限制 15S*/
    private static final int RUN_TIME_LIMITED = 15;
    /*线程池线程数*/
    private static final int N_THREAD = 5;
    /*执行传过来的代码的线程池*/
    private static final ExecutorService pool = new ThreadPoolExecutor(N_THREAD,
            N_THREAD,
            60L,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<Runnable>(N_THREAD));
    /*超时警告*/
    private static final String WAIT_WARNING = "服务器繁忙，请稍后提交";
    /*除了模板代码之外没有其他输入代码*/
    private static final String NO_INPUT = "没有输入";
    public String execute(String sourceCode){
        //运行结果收集
        DiagnosticCollector<JavaFileObject> compilerCollector =
                new DiagnosticCollector<>();
        //编译源代码 生成字节码
        byte[] classBytes = Compiler.compile(sourceCode,compilerCollector);
        if(classBytes==null){
            //获取编译错误信息
            List<Diagnostic<?extends JavaFileObject>> complieError
                    = compilerCollector.getDiagnostics();
            //错误信息
            StringBuilder errorInfo = new StringBuilder();
            for (Diagnostic diagnostic:complieError){
                errorInfo.append("Compile error at");
                errorInfo.append(diagnostic.getLineNumber());
                errorInfo.append(".");
                errorInfo.append(System.lineSeparator());
            }
            return errorInfo.toString();
        }
        return "";
    }

}
