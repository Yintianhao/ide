package com.mine.ide.service.implement;

import com.mine.ide.dao.RunRecordDAO;
import com.mine.ide.dao.UserDAO;
import com.mine.ide.entity.RunRecord;
import com.mine.ide.entity.User;
import com.mine.ide.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author yintianhao
 * @createTime 20190617 15:07
 * @description
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserDAO userDAO;
    @Autowired
    RunRecordDAO recordDAO;
    @Override
    public boolean login(String userid, String password) {
        try {
            return userDAO.selectUser(userid).equals(password);
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public boolean register(User user) {
        try {
            if (userDAO.selectUser(user.getUserid())!=null){
                return false;
            }
            userDAO.insertUser(user);
        }catch (Exception e){
            return false;
        }
        return true;
    }

    @Override
    public boolean runCode(RunRecord record) {
        try {
            recordDAO.insertRecord(record);
            return true;
        }catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<RunRecord> queryRecord(String userid) {
        try {
            return recordDAO.selectRecord(userid);
        }catch (Exception e){
            System.out.println("select error");
            e.printStackTrace();
            return null;
        }
    }
}
