package com.zhangyu.community;

import com.zhangyu.community.dao.UserMapper;
import com.zhangyu.community.entity.DiscussPost;
import com.zhangyu.community.entity.User;
import com.zhangyu.community.service.DiscussPostService;
import com.zhangyu.community.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;

/**
 * @author: zhang
 * @date: 2022/5/6
 * @description:
 */

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class CaffeineTests {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserMapper userMapper;

    @Test
    public void initDataForTest() {
        for (int i = 0; i < 300000; i++) {
            DiscussPost post = new DiscussPost();
            post.setUserId(111);
            post.setTitle("互联网求职暖春计划");
            post.setContent("别再传播焦虑啦，今年是很难，明年也很难，后年也很难，没关系一切都会好起来的！");
            post.setCreateTime(new Date());
            post.setScore(Math.random() * 2000);
            discussPostService.addDiscussPost(post);
        }
    }

    @Test
    public void testCache() {
        System.out.println(discussPostService.findDiscussPosts(0, 0, 10, 1));
        System.out.println(discussPostService.findDiscussPosts(0, 0, 10, 1));
        System.out.println(discussPostService.findDiscussPosts(0, 0, 10, 1));
        System.out.println(discussPostService.findDiscussPosts(0, 0, 10, 0));
    }

    @Test
    public void addUser() {
        User user = new User();
        for (int i = 5; i < 50; i++) {
            user.setUsername("zhangyu" + i);
            user.setStatus(0);
            user.setType(2);
            user.setPassword("86e890b0a7bd1c96b2445c68efa79236");
            user.setSalt("4dd72");
            user.setEmail("123@qq.com");
            userMapper.insertUser(user);
        }
    }


}
