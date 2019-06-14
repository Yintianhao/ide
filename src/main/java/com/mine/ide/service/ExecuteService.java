package com.mine.ide.service;

import com.mine.ide.util.Compiler;
import com.mine.ide.util.JavaClassExecutor;
import org.springframework.stereotype.Service;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import java.util.List;
import java.util.concurrent.*;


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
    private static final String WAIT_WARNING = "Server busy";
    /*除了模板代码之外没有其他输入代码*/
    private static final String NO_INPUT = "No input";
    public String execute(String sourceCode){
        //运行结果收集
        DiagnosticCollector<JavaFileObject> compilerCollector =
                new DiagnosticCollector<>();
        //编译源代码 生成字节码
        byte[] classBytes = Compiler.compile(sourceCode,compilerCollector);
        if(classBytes==null){
            //获取编译错误信息
            List<Diagnostic<?extends JavaFileObject>> compileError
                    = compilerCollector.getDiagnostics();
            //错误信息
            StringBuilder errorInfo = new StringBuilder();
            for (Diagnostic diagnostic: compileError){
                errorInfo.append("Compile error at line ");
                errorInfo.append(diagnostic.getLineNumber());
                errorInfo.append(".");
                errorInfo.append(System.lineSeparator());
            }
            return errorInfo.toString();
        }
        Callable<String> runTask = new Callable<String>() {
            @Override
            public String call() throws Exception {
                return JavaClassExecutor.execute(classBytes);
            }
        };
        Future<String> res = null;
        try {
            res = pool.submit(runTask);
        }catch (RejectedExecutionException e){
            return WAIT_WARNING;
        }
        String runResult;
        try {
            runResult = res.get(RUN_TIME_LIMITED,TimeUnit.SECONDS);
        }catch (InterruptedException e){
            runResult = "Program interrupted";
        }catch (ExecutionException e){
            runResult = e.getCause().getMessage();
        }catch (TimeoutException e){
            runResult = "Time limited exceeded";
        }finally {
            res.cancel(true);
        }
        return runResult !=null?runResult:NO_INPUT;
    }
}
