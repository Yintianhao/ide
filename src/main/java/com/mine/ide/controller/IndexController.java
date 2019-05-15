package com.mine.ide.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * @author yintianhao
 * @createTime 20190417 14:42
 * @description 测试控制器
 */
@Controller
public class IndexController {

    private static String defaultSourceCode = "public class Run {\n"
            + "         public static void main(String[] args) {\n"
            + "             \n"
            + "         }\n"
            +       "}";
    @RequestMapping("/")
    public String defaultPage(Model model){

        //.addAttribute("lastSourceCode",defaultSourceCode);
        //model.addAttribute("runResult","");
        return "test";
    }
    @RequestMapping("/check")
    public String login(){
        return "test";

    }

    @RequestMapping(value = "/run")
    public String showIde(@RequestParam("sourceCode") String sourceCode,Model model){
        System.out.println("传过来的源代码："+sourceCode);
        return "host";
    }

}
