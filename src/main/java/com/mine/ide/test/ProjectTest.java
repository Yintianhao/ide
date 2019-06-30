package com.mine.ide.test;

import com.mine.ide.service.implement.ExecuteSqlService;
import com.mine.ide.util.CLangUtil;
import com.mine.ide.util.SqlUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author yintianhao
 * @createTime 20190417 15:36
 * @description 单元测试
 */
public class ProjectTest {

    private static final String C_CODE ="#include <stdio.h>\n" +
            "int main(){\n" +
            "\tfor(;;)\n" +
            "\tprintf(\"Hello world!\");\n" +
            "\treturn 0;\n" +
            "}";
    @Autowired
    ExecuteSqlService sqlService;
    public void runCLang(){
        Executor executor = Executors.newSingleThreadExecutor();
        FutureTask<String> futureTask = new FutureTask<String>(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return CLangUtil.runCLangCode(C_CODE);
            }
        });
        executor.execute(futureTask);
        try{
            String result=futureTask.get(10, TimeUnit.SECONDS);
            System.out.println(result);
        }catch (InterruptedException e) {
            // TODO: handle exception
            System.out.println("方法执行中断");
            // future.cancel(true);
        }catch (ExecutionException e) {
            System.out.println("Excution异常");
            // TODO: handle exception
            futureTask.cancel(true);
        }catch (TimeoutException e) {
            // TODO: handle exception
            System.out.println("方法执行时间超时");
            //future.cancel(true);
        }
        System.out.println(CLangUtil.runCLangCode(C_CODE));
    }
    @Test
    public void testRedis(){
        //连接redis ，redis的默认端口是6379
        Jedis jedis = new Jedis ("localhost",6379);
        //验证密码，如果没有设置密码这段代码省略
        //jedis.auth("password");
        jedis.connect();//连接
        //将值value插入到列表key的表头。
        jedis.expire("key3",3);
        jedis.lpush("key3", "value1");
        jedis.lpush("key3", "value2");
        jedis.lpush("key3", "value3");
        List list = jedis.lrange("key3", 0, -1);//stop下标也在取值范围内(闭区间)
        for(int i=0;i<list.size();i++){
            System.out.println(list.get(i));
        }
        jedis.disconnect();
    }
    @Test
    public void test(){
        String sql = "select from user where id = 1234;delete it where id = 1213;delete it where id = 1213;";
        HashMap<String,ArrayList<String>> res = SqlUtil.split(sql);
        for (Map.Entry entry:res.entrySet()){
            System.out.print(entry.getKey());
            System.out.println(entry.getValue());
        }
    }
    @Test
    public void numberTest(){
        long a = 0x205a1c;
        long b = a;
        a = a>>5;
        System.out.println(Long.toHexString(a));
        b = b>>2;
        a = a<<3;
        System.out.println(b-a);
    }
    @Test
    public void numberTest1(){
        long a = 0x200fffff;
        long base = 0x22000000;
        long later = base+((((a-0x20000000)<<3)+0)<<2);
        System.out.println(Long.toHexString(later));
    }

}
