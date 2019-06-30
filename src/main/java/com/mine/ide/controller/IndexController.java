package com.mine.ide.controller;
import com.mine.ide.entity.RunRecord;
import com.mine.ide.service.implement.ExecuteJavaService;
import com.mine.ide.service.interfaces.UserService;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
/**
 * @author yintianhao
 * @createTime 20190417 14:42
 * @description
 */
@Controller
public class IndexController {
    private static String defaultSourceCode = "\n"+"public class Run {\n"
            + "         public static void main(String[] args) {\n"
            + "             \n"
            + "         }\n"
            +       "}";
    private List<RunRecord> runRecords = new ArrayList<>();
    @Autowired
    ExecuteJavaService executorService;
    @Autowired
    UserService userService;
    private static final Logger log = Logger.getLogger(IndexController.class);

    @RequestMapping("/")
    public String home(Model model, HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        Cookie id = null;
        Cookie pd = null;
        if (cookies!=null){
            for (Cookie cookie:cookies){
                log.info("cookie name "+cookie.getName());
                if (cookie.getName().equals("userid"))
                    id = cookie;
                if (cookie.getName().equals("password"))
                    pd = cookie;
            }
        }
        try {
            if (userService.login(id.getValue(),pd.getValue())){
                model.addAttribute("lastSourceCode",defaultSourceCode);
                model.addAttribute("runResult","");
                return "java";
            }
        }catch (NullPointerException e){
            log.info("null pointer");
        }
        return "login";
    }

    @RequestMapping("/ide")
    public String defaultPage(Model model){
        model.addAttribute("lastSourceCode",defaultSourceCode);
        model.addAttribute("runResult","");
        return "java";
    }
    @RequestMapping("/toJava")
    public String toJava(Model model){
        model.addAttribute("lastSourceCode",defaultSourceCode);
        return "java";
    }
}
