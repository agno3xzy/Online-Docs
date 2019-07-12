package com.OnlineDocs.service.Impl;

import com.OnlineDocs.dao.CooperateMapper;
import com.OnlineDocs.dao.UserInfoMapper;
import com.OnlineDocs.entity.Cooperate;
import com.OnlineDocs.entity.UserInfo;
import com.OnlineDocs.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

public class UserInfoServiceImpl implements UserInfoService {
    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public List<UserInfo> getUserByName(String username){
        Example example = new Example(UserInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userName", username);
        return userInfoMapper.selectByExample(example);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public boolean addUser(String username, String password, String email) {
        try {
            userInfoMapper.addUserInfo(username, password, email);
        } catch(Exception e)
        {
            return false;
        }
        return true;
    }

    @Override
    public List<UserInfo> getUserByID(int userID) {
        Example example = new Example(UserInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("iduser",userID);
        return userInfoMapper.selectByExample(example);
    }

    @Override
    public List<UserInfo> getAllUser() {
        UserInfo userinfo = new UserInfo();
        return userInfoMapper.select(userinfo);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public boolean modifyKey(String username, String password) {
        try {
            userInfoMapper.modifyKey(username, password);
        } catch(Exception e)
        {
            return false;
        }
        return true;
    }
}
