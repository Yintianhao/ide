package com.mine.ide.dao;

import com.mine.ide.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;

/**
 * @author yintianhao
 * @createTime 20190617 11:18
 * @description 用户登录注册DAO
 */
@Mapper
public interface UserDAO {
    @Insert("insert into user(userid,password) values (#{userid},#{password})")
    public void insertUser(User user);
    @Select("select password from user where userid = #{userid}")
    @ResultType(String.class)
    public String selectUser(String userid);
}
