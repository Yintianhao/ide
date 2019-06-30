package com.mine.ide.util;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.concurrent.*;

/**
 * @author yintianhao
 * @createTime 20190624 19:40
 * @description C语言编译工具
 */
public class CLangUtil {
    private static final Logger log = Logger.getLogger(CLangUtil.class);

    //代码存放路径
    public static final String CODE_PATH = "D:\\springboot2\\code\\";


    private static boolean generateCFile(String content){
        BufferedWriter out = null;
        try{
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
    public static String runCLangCode(String sourceCode){
        generateCFile(sourceCode);
        Executor executor = Executors.newSingleThreadExecutor();
        FutureTask<String> futureTask = new FutureTask<String>(new Callable<String>() {
            @Override
            public String call() throws Exception {
                String compileResult = execCmd("gcc -o "+CODE_PATH+"test "+CODE_PATH+"test.c",null);
                if (compileResult.equals(""))
                    return execCmd(CODE_PATH+"test.exe",null).replaceAll("\n","<br/>").replaceAll(" ","&nbsp;");
                else {
                    int errorIndex = compileResult.indexOf("error");
                    return compileResult.substring(errorIndex).replaceAll("\n","<br/>").replaceAll(" ","&nbsp;");
                }
            }
        });
        executor.execute(futureTask);
        try {
            execCmd("taskkill /f /im test.exe",null);
            log.info("killed test.exe");
        }catch (Exception e){
            e.printStackTrace();
        }
        String result = "";
        try{
            result=futureTask.get(10, TimeUnit.SECONDS);
        }catch (InterruptedException e) {
            // TODO: handle exception
            log.info("Interrupt");
            result = "程序中断，请检查是否有内存冲突等错误";
            // future.cancel(true);
        }catch (ExecutionException e) {
            result = "程序执行错误";
            futureTask.cancel(true);
        }catch (TimeoutException e) {
            // TODO: handle exception
            result = "时间超限，请检查是否存在无限循环等程序无法自动结束的情况";
        }
        log.info("result - "+result);
        return result.equals("")?"没有输出":result;
    }
    /**
     * 执行系统命令, 返回执行结果
     *
     * @param cmd 需要执行的命令
     * @param dir 执行命令的子进程的工作目录, null 表示和当前主进程工作目录相同
     */
    private static String execCmd(String cmd, File dir) throws Exception {
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

    private static void closeStream(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (Exception e) {
                // nothing
            }
        }
    }

}
