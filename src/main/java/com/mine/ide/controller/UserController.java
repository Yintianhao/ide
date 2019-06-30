package com.mine.ide.controller;

import com.mine.ide.entity.RunRecord;
import com.mine.ide.entity.User;
import com.mine.ide.service.implement.ExecuteCLangService;
import com.mine.ide.service.implement.ExecuteJavaService;
import com.mine.ide.service.implement.ExecuteSqlService;
import com.mine.ide.service.interfaces.UserService;
import com.mine.ide.util.RedisUtil;
import com.mine.ide.util.TimeUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yintianhao
 * @createTime 20190617 20:14
 * @description 用户行为控制器
 */
@Controller
public class UserController {

    private static final String DEFAULT_JAVA_SOURCE_CODE = "\n"+"public class Run {\n"
            + "         public static void main(String[] args) {\n"
            + "             \n"
            + "         }\n"
            +       "}";
    private static final String DEFAULT_SQL_SOURCE_CODE = "select * from user";

    private static final String DEFAULT_CLANG_SOURCE_CODE = "#include <stdio.h>\n" +
            "int main(){\n" +
            "\tprintf(\"Hello world!\");\n" +
            "\treturn 0;\n" +
            "}";

    //log
    private static final Logger log = Logger.getLogger(UserController.class);

    @Autowired
    ExecuteJavaService javaService;
    @Autowired
    UserService userService;
    @Autowired
    ExecuteSqlService sqlService;
    @Autowired
    ExecuteCLangService cLangService;

    @SuppressWarnings(value = "all")
    @Autowired
    RedisUtil redisUtil;

    private static final String KEY_PREFIX_CODE = "code";
    private static final String KEY_PREFIX_TIME = "time";
    private static final String KEY_PREFIX_LANG = "language";

    @RequestMapping("/runInfo")
    public String showSiteInfo(Model model,
                               HttpServletRequest request){
        String userid = getUserId(request);
        List<RunRecord> runRecords = userService.queryRecord(getUserId(request));
        log.info("redis size "+redisUtil.lRange(KEY_PREFIX_CODE+userid,0,-1).size());
        log.info("mysql size "+runRecords.size());
        if (redisUtil.lRange(KEY_PREFIX_CODE+userid,0,-1).size()==runRecords.size()){
            //直接从内存中取
            log.info("from redis");
            List<String> codes = redisUtil.lRange(KEY_PREFIX_CODE+userid,0,-1);
            List<String> times = redisUtil.lRange(KEY_PREFIX_TIME+userid,0,-1);
            List<String> langs = redisUtil.lRange(KEY_PREFIX_LANG+userid,0,-1);
            log.info(langs);
            runRecords.clear();
            for (int i = 0;i < codes.size();i++){
                runRecords.add(new RunRecord(userid,codes.get(i),times.get(i),langs.get(i)));
            }
        }else{
            log.info("from mysql");
            //runRecords = userService.queryRecord(getUserId(request));
            clearRedis(userid);
            for(RunRecord record : runRecords){
                redisUtil.rPush(KEY_PREFIX_CODE+userid,record.getCode());
                redisUtil.rPush(KEY_PREFIX_TIME+userid,record.getTimedetail());
                redisUtil.rPush(KEY_PREFIX_LANG+userid,record.getLanguage());
            }
        }
        model.addAttribute("runRecords",runRecords);
        return "admin";
    }
    @RequestMapping(value = "/run",method = RequestMethod.POST)
    public String showIde(@RequestParam("sourceCode") String sourceCode,
                          Model model,
                          HttpServletRequest request){
        //如果redis数据和mysql不一致，将不一致的数据更新至redis
        synDataBase(sourceCode,"JAVA",request);
        String runResult = javaService.execute(sourceCode);
        runResult = runResult.replaceAll(System.lineSeparator(),"<br/>");//Html空行
        model.addAttribute("lastSourceCode",sourceCode);
        model.addAttribute("runResult",runResult);
        return "java";
    }
    @RequestMapping("/login")
    public String login(@RequestParam("userid")String userid,
                        @RequestParam("password")String password,
                        String remember,
                        Model model,
                        HttpServletResponse response,
                        HttpServletRequest request){

        if (userService.login(userid,password)){
            if (remember!=null&&remember.equals("on")){
                Cookie id = new Cookie("userid",userid);
                id.setPath("/");
                id.setDomain("localhost");
                Cookie pd = new Cookie("password",password);
                pd.setPath("/");
                pd.setDomain("localhost");
                id.setMaxAge(7*24*60*60);
                pd.setMaxAge(7*24*60*60);
                response.addCookie(id);
                response.addCookie(pd);
            }else {
                //删除
                Cookie[] cookies = request.getCookies();
                if (cookies!=null&&cookies.length!=0){
                    for (int i = 0;i < cookies.length;i++){
                        cookies[i].setMaxAge(60*60);
                        cookies[i].setDomain("localhost");
                        cookies[i].setPath("/");
                        response.addCookie(cookies[i]);
                    }
                }
            }
            model.addAttribute("lastSourceCode", DEFAULT_JAVA_SOURCE_CODE);
            return "java";
        }
        return "login";
    }
    @RequestMapping("/logout")
    public String logout(HttpServletRequest request,
                         HttpServletResponse response){
        //登出
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie:cookies){
            cookie.setMaxAge(0);
            cookie.setDomain("localhost");
            cookie.setPath("/");
            response.addCookie(cookie);
        }
        String userid = getUserId(request);
        clearRedis(userid);
        System.out.println("redis data size ="+redisUtil.lRange(KEY_PREFIX_CODE+getUserId(request),0,-1).size());
        return "login";
    }

    @RequestMapping("/toRegister")
    public String toRegisterPage(){
        return "register";
    }
    @RequestMapping("/register")
    public String register(@RequestParam("userid")String userid,
                           @RequestParam("password1")String password1,
                           @RequestParam("password2")String password2,
                           Model model){
        if (!password1.equals(password2)){
            return "register";
        }
        else {
            try {
                if(userService.register(new User(userid,password1))){
                    model.addAttribute("lastSourceCode", DEFAULT_JAVA_SOURCE_CODE);
                    return "java";
                } else{
                    model.addAttribute("errorInfo","系统错误无法插入");
                    return "page_error";
                }
            }catch (Exception e){
                model.addAttribute("errorInfo","异常");
                return "page_error";
            }
        }
    }
    @RequestMapping("/sql")
    public String toSqlPage(Model model){
        model.addAttribute("lastSourceCode",DEFAULT_SQL_SOURCE_CODE);
        return "sql";
    }
    @RequestMapping(value = "/runSql",method = RequestMethod.POST)
    public String runSql(@RequestParam("sourceCode") String sourceCode,
                         Model model,
                         HttpServletRequest request){
        //运行结果
        String runResult = sqlService.runSQL(sourceCode.toLowerCase());;
        runResult = runResult.replaceAll(System.lineSeparator(),"<br/>");//Html空行
        //如果redis数据和mysql不一致，将不一致的数据更新至redis
        synDataBase(sourceCode,"SQL",request);
        model.addAttribute("lastSourceCode",sourceCode);
        model.addAttribute("runResult",runResult);
        return "sql";
    }
    @RequestMapping("/clang")
    public String toCLangPage(Model model){
        model.addAttribute("lastSourceCode",DEFAULT_CLANG_SOURCE_CODE);
        return "clang";
    }
    @RequestMapping("/runCLang")
    public String runCLang(@RequestParam("sourceCode") String sourceCode,
                         Model model,
                         HttpServletRequest request){
        synDataBase(sourceCode,"C",request);
        String result = cLangService.getRunResult(sourceCode);
        model.addAttribute("lastSourceCode",sourceCode);
        model.addAttribute("runResult",result);
        return "clang";
    }

    private String getUserId(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie:cookies){
            if (cookie.getName().equals("userid"))
                return cookie.getValue();
        }
        return "";
    }
    private void synDataBase(String sourceCode,String language,HttpServletRequest request){
        //如果redis数据和mysql不一致，将不一致的数据更新至redis
        String userid = getUserId(request);
        userService.runCode(new RunRecord(userid,sourceCode,TimeUtils.getTimeInfo(),language));
        List<RunRecord> recordMysql = userService.queryRecord(userid);
        List<String> recordRedis = redisUtil.lRange(KEY_PREFIX_CODE+userid,0,-1);
        redisUtil.expire(KEY_PREFIX_CODE+userid,1*60);
        redisUtil.expire(KEY_PREFIX_LANG+userid,1*60);
        redisUtil.expire(KEY_PREFIX_TIME+userid,1*60);
        try {
            int offset = recordMysql.size()-recordRedis.size();
            if (offset!=0){
                clearRedis(userid);
                for (int i = 0;i < recordMysql.size();i++){
                    redisUtil.rPush(KEY_PREFIX_CODE+userid,recordMysql.get(i).getCode());
                    redisUtil.rPush(KEY_PREFIX_TIME+userid,recordMysql.get(i).getTimedetail());
                    redisUtil.rPush(KEY_PREFIX_LANG+userid,recordMysql.get(i).getLanguage());
                }
                log.info("redis data is different from mysql");
                //System.out.println("Redis和Mysql的数据不一致");
            }
        }catch (NullPointerException e){
            log.info("null pointer");
        }
    }
    public void clearRedis(String userid){
        for (int i = 0;;i++){
            redisUtil.rPop(KEY_PREFIX_CODE+userid);
            redisUtil.rPop(KEY_PREFIX_TIME+userid);
            redisUtil.rPop(KEY_PREFIX_LANG+userid);
            if (redisUtil.lRange(KEY_PREFIX_CODE+userid,0,-1).size()==0)
                break;
        }
    }

}
