package com.mine.ide.service.implement;

import com.mine.ide.util.CLangUtil;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.concurrent.*;

/**
 * @author yintianhao
 * @createTime 20190625 11:14
 * @description ExecuteCLang
 */
@Service
public class ExecuteCLangService {
    private static final Logger log = Logger.getLogger(ExecuteCLangService.class);

    //线程池大小
    private static final int THREAD_COUNT = 10;
    //超时时间 s
    private static final int RUN_TIME_LIMIT = 10;

    private static final String INTERRUPT_ERROR = "programme interrupted";

    private static final String TIMEOUT_ERROR = "time out";

    private static final String EXECUTE_ERROR = "execute error";

    //线程池
    private static final ExecutorService executePool = new ThreadPoolExecutor(
            THREAD_COUNT,
            THREAD_COUNT,
            60L,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<Runnable>(THREAD_COUNT)
    );

    //代码存放路径
    public static final String CODE_PATH = "D:\\springboot2\\code\\";

    /**
     * @param content C代码
     * */
    private boolean generateCFile(String content){
        BufferedWriter out = null;
        try{
            //写入
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(CODE_PATH+"test.c", false)));
            out.write(content);
        }catch (Exception e){
            log.error(e.getCause().getMessage());
            return false;
        }finally {
            try {
                out.close();
            }catch (IOException e){
                log.error(e.getCause().getMessage());
                return false;
            }
        }
        return true;
    }
    public String getRunResult(String sourceCode){
        Callable<String> task = new Callable<String>() {
            @Override
            public String call() throws Exception {
                return runCode(sourceCode);
            }
        };
        executePool.submit(task);
        Future<String> runFuture = null;
        try {
            runFuture = executePool.submit(task);
        }catch (RejectedExecutionException e){
            e.getMessage();
        }
        String result = null;
        try {
            result = runFuture.get(RUN_TIME_LIMIT,TimeUnit.SECONDS);
        }catch (InterruptedException e){
            result = INTERRUPT_ERROR;
            e.printStackTrace();
        }catch (ExecutionException e){
            result = EXECUTE_ERROR;
        }catch (TimeoutException e){
            result = TIMEOUT_ERROR;
        }
        return result;
    }
    public String runCode(String sourceCode){
        generateCFile(sourceCode);
        //编译C文件
        String compileResult = null;
        try {
            compileResult = execCmd("gcc -o "+CODE_PATH+"test "+CODE_PATH+"test.c",null);
        } catch (Exception e) {
            log.info("gcc 调用出错");
            e.printStackTrace();
        }
        if (compileResult.equals(""))//编译不出错的情况，replaceAll将\n换成HTML的换行，空格换成HTML的空格
        {
            try {
                return execCmd(CODE_PATH+"test.exe",null).replaceAll("\n","<br/>").replaceAll(" ","&nbsp;");
            } catch (Exception e) {
                log.info("C程序运行出错");
                e.printStackTrace();
            }
        }
        else {
            //编译出错，找到error的位置，返回error及其后的信息
            int errorIndex = compileResult.indexOf("error");
            return compileResult.substring(errorIndex).replaceAll("\n","<br/>").replaceAll(" ","&nbsp;");
        }
        return "";
    }
    /**
     * 执行系统命令, 返回执行结果
     * @param cmd 需要执行的命令
     * @param dir 执行命令的子进程的工作目录, null 表示和当前主进程工作目录相同
     */
    private String execCmd(String cmd, File dir) throws Exception {
        StringBuilder result = new StringBuilder();
        Process process = null;
        BufferedReader bufrIn = null;
        BufferedReader bufrError = null;
        try {
            // 执行命令, 返回一个子进程对象（命令在子进程中执行）
            process = Runtime.getRuntime().exec(cmd, null, dir);
            // 方法阻塞, 等待命令执行完成（成功会返回0）
            process.waitFor();
            // 获取命令执行结果, 有两个结果: 正常的输出 和 错误的输出（PS: 子进程的输出就是主进程的输入）
            bufrIn = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
            bufrError = new BufferedReader(new InputStreamReader(process.getErrorStream(), "UTF-8"));
            // 读取输出
            String line = null;
            while ((line = bufrIn.readLine()) != null) {
                result.append(line).append('\n');
            }
            while ((line = bufrError.readLine()) != null) {
                result.append(line).append('\n');
            }
        } finally {
            closeStream(bufrIn);
            closeStream(bufrError);
            // 销毁子进程
            if (process != null) {
                process.destroy();
            }
        }
        // 返回执行结果
        return result.toString();
    }
    private void closeStream(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (Exception e) {
                // nothing
            }
        }
    }
}
