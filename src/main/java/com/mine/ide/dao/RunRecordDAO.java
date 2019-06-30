package com.mine.ide.dao;

import com.mine.ide.entity.RunRecord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author yintianhao
 * @createTime 20190617 19:38
 * @description 代码运行记录DAO
 */
@Mapper
public interface RunRecordDAO {
    //取出该用户的代码运行记录
    @Select("select * from runrecord where userid = #{userid}")
    @ResultType(RunRecord.class)
    public List<RunRecord> selectRecord(String userid);
    //插入用户代码运行记录
    @Insert("insert into runrecord (userid,code,timedetail,language) values " +
            "(#{userid},#{code},#{timedetail},#{language})")
    public void insertRecord(RunRecord record);

}
