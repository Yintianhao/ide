package com.mine.ide.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author yintianhao
 * @createTime 20190417 14:42
 * @description 测试控制器
 */
@Controller
public class IndexController {

    @RequestMapping(value = "/run")
    public String showIde(){
        return "host";
    }
}
