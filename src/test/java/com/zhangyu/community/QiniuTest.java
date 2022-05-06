package com.zhangyu.community;

import com.zhangyu.community.dao.UserMapper;
import com.zhangyu.community.entity.User;
import com.zhangyu.community.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author: zhang
 * @date: 2022/5/6
 * @description:
 */

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class QiniuTest {


    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Test
    public void mysqlTest() {
        int res = userService.updateHeaderUrl(14, "zhangyu");
        int zhangyu = userMapper.updateHeader(14, "zhangyu");
        int status = userMapper.updateStatus(3, 1);
        System.out.println(res + "," + zhangyu + "," + status);
    }

    @Test
    public void mysqlTest2() {
        int res = userService.updateHeaderUrl(14, "http://rb3e6885q.hn-bkt.clouddn.com/7a457f1877af419a931f3fc24ebec624");
        System.out.println(res);
        User userById = userService.findUserById(14);
        System.out.println(userById);
    }

    @Test
    public void clearCache() {
    }
}
