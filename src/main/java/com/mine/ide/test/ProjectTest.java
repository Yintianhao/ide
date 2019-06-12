package com.mine.ide.test;

import com.mine.ide.service.ExecuteService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
/**
 * @author yintianhao
 * @createTime 20190417 15:36
 * @description 单元测试
 */

public class ProjectTest {

    @Test
    public void run(){
       String code = "public class HelloWorld {\n" +
               "    public static void main(String []args) {\n" +
               "       System.out.println(\" \");\n" +
               "    }\n" +
               "}";
        ExecuteService service = new ExecuteService();
        print(service.execute(code));
    }
    private void print(String str){
        System.out.println(str);
    }
}
