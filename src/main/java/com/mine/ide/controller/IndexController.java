package com.mine.ide.controller;

import com.mine.ide.service.ExecuteService;
import com.mine.ide.test.ProjectTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author yintianhao
 * @createTime 20190417 14:42
 * @description 测试控制器
 */
@Controller
public class IndexController {

    private static String defaultSourceCode = "\n"+"public class Run {\n"
            + "         public static void main(String[] args) {\n"
            + "             \n"
            + "         }\n"
            +       "}";
    private AtomicInteger count = new AtomicInteger(523);
    @Autowired
    ExecuteService executorService;
    @RequestMapping("/")
    public String home(){
        count.incrementAndGet();
        System.out.println("第"+count.get()+"位访客");
        return "home";
    }
    @ResponseBody
    @RequestMapping("/count")
    public String getGuestCount(){
        return count.get()+"";
    }
    @RequestMapping("/ide")
    public String defaultPage(Model model){
        model.addAttribute("lastSourceCode",defaultSourceCode);
        model.addAttribute("runResult","");
        return "host";
    }
    @RequestMapping(value = "/run",method = RequestMethod.POST)
    public String showIde(@RequestParam("sourceCode") String sourceCode,Model model){
        ProjectTest.print(System.getProperty("java.home"));
        model.addAttribute("lastSourceCode",sourceCode);
        model.addAttribute("runResult",executorService.execute(sourceCode).replaceAll(System.lineSeparator(),"<br/>"));
        return "host";
    }

}
