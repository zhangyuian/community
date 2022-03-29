package com.zhangyu.community.utils;

import com.zhangyu.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * @author: zhang
 * @date: 2022/3/25
 * @description:
 */

@Component
public class HostHolder {

    ThreadLocal<User> map = new ThreadLocal<>();

    public User getUser(){
        return map.get();
    }

    public void setUser(User user){
        map.set(user);
    }

    public void clear(){
        map.remove();
    }

}
