package com.mine.ide.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author yintianhao
 * @createTime 20190417 14:42
 * @description 测试控制器
 */
@Controller
public class IndexController {

    @RequestMapping("/")
    public String showIde(){
        return "test";
    }
}
