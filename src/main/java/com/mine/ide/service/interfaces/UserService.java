package com.mine.ide.service.interfaces;

import com.mine.ide.entity.RunRecord;
import com.mine.ide.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author yintianhao
 * @createTime 20190617 15:03
 * @description 用户服务
 */
public interface UserService {
    public boolean login(String userid,String password);
    public boolean register(User user);
    public boolean runCode(RunRecord record);
    public List<RunRecord> queryRecord(String userid);
}
