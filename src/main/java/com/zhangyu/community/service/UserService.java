package com.zhangyu.community.service;

import com.zhangyu.community.dao.UserMapper;
import com.zhangyu.community.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: zhang
 * @date: 2022/3/22
 * @description:
 */

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public User findUserById(int id) {
        return userMapper.selectById(id);
    }


}
