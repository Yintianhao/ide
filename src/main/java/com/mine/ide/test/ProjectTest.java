package com.mine.ide.test;

import com.mine.ide.service.ExecuteService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.tools.ToolProvider;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import static java.util.logging.Level.FINE;

/**
 * @author yintianhao
 * @createTime 20190417 15:36
 * @description 单元测试
 */

public class ProjectTest {

    @Test
    public void run(){
        print(System.getProperty("java.home"));
        File file = new File(System.getProperty("java.home"));
        file = file.getParentFile();
        print(file.getName());
        for (String name : new String[]{"lib","tool.jar"})
            file = new File(file, name);
        if (!file.exists())
            print("not exit");
        try {
            URL[] urls = { file.toURI().toURL() };
            print(file.toURI().toURL().toString());
        }catch (MalformedURLException e){

        }
    }
    public static void print(String str){
        System.out.println(str);
    }
}
