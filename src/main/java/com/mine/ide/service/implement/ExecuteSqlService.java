package com.mine.ide.service.implement;

import com.mine.ide.util.SqlUtil;
import com.mine.ide.util.TimeUtils;
import org.omg.CORBA.TIMEOUT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Service;
import org.springframework.test.context.jdbc.Sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author yintianhao
 * @createTime 20190620 21:51
 * @description ExecuteSqlService
 */
@Service
public class ExecuteSqlService {

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
    @Autowired
    JdbcTemplate jdbcTemplate;

    public String runSQL(String sql){
        Callable<String> task = new Callable<String>() {
            @Override
            public String call() throws Exception {
                return compile(sql);
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

    public String compile(String sql){
        //分割出的SQL语句
        HashMap<String,ArrayList<String>> compileSet = SqlUtil.split(sql);
        String res = " ";
        //结果集
        List<List<Map<String,Object>>> resultSet = new ArrayList<>();
        for(Map.Entry entry:compileSet.entrySet()){
            if (SqlUtil.CREATE.equals(entry.getKey())
                    ||SqlUtil.DELETE.equals(entry.getKey())
                    ||SqlUtil.DROP.equals(entry.getKey())
                    ||SqlUtil.UPDATE.equals(entry.getKey())
                    ||SqlUtil.INSERT.equals(entry.getKey())
                    ||SqlUtil.USE.equals(entry.getKey())){
                //create update delete drop 直接执行
                try {
                    ArrayList<String> list = (ArrayList<String>) entry.getValue();
                    for (String str:list){
                        //System.out.println(str);
                        jdbcTemplate.execute(str);
                    }
                }catch (Exception e){
                    return e.getCause().getMessage();//返回错误信息
                }
            }else if (SqlUtil.SELECT.equals(entry.getKey())||SqlUtil.SHOW.equals(entry.getKey())){
                try {
                    ArrayList<String> list = (ArrayList<String>) entry.getValue();
                    for (String str:list){
                        System.out.println(str);
                        resultSet.add(jdbcTemplate.queryForList(str));
                    }
                }catch (Exception e){
                    return e.getCause().getMessage();
                }
            }
            res = getQueryList(resultSet);
        }
        return res.equals(" ")?"Execute ok!":res;
    }
    public String getQueryList(List<List<Map<String,Object>>> resultSet){
        StringBuilder builder = new StringBuilder();
        for (List<Map<String,Object>> list:resultSet){
            for (Map<String,Object> map:list){
                for (Map.Entry entry:map.entrySet()){
                            builder.append(String.valueOf(entry.getValue()).replace("\n",""));
                    builder.append("          ");
                }
                builder.append("\n");
            }
        }
        return builder.toString().replaceAll("\n","<br/>").replaceAll(" ","&nbsp;");
    }
}
