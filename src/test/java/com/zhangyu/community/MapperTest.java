package com.zhangyu.community;

import com.zhangyu.community.dao.DiscussPostMapper;
import com.zhangyu.community.dao.UserMapper;
import com.zhangyu.community.entity.DiscussPost;
import com.zhangyu.community.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author: zhang
 * @date: 2022/3/21
 * @description:
 */

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTest {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Test
    public void testSelectUser() {
        User user = userMapper.selectById(1);
        System.out.println(user);

        User user1 = userMapper.selectByName("zhangyu");
        System.out.println(user1);

        User user2 = userMapper.selectByEmail("437489599@qq.com");
        System.out.println(user2);
    }

    @Test
    public void testInsertUser() {
        User user = new User();
        user.setUsername("test");
        user.setPassword("123456");
        user.setSalt("abc");
        user.setEmail("test@qq.com");
        user.setHeaderUrl("https://img-blog.csdnimg.cn/20201123195242899.png");
        user.setCreateTime(new Date());

        int rows = userMapper.insertUser(user);
        System.out.println(rows);
        System.out.println(user.getId());
    }

    @Test
    public void updateUser() {
        int rows = userMapper.updateStatus(2, 1);
        System.out.println(rows);

        rows = userMapper.updateHeader(2, "https://img-blog.csdnimg.cn/2.png");
        System.out.println(rows);

        rows = userMapper.updatePassword(2, "hello");
        System.out.println(rows);
    }

    @Test
    public void maopao(){
        int[] nums = {1,2,3,4,5,6,7,8,9};
        for(int i = 0; i < nums.length - 1; i++) {
            for(int j = 0; j < nums.length - 1 - i; j++) {
                if(nums[j] < nums[j + 1]) {
                    int temp = nums[j];
                    nums[j] = nums[j+1];
                    nums[j+1] = temp;
                }
            }
        }
        for (int num : nums) {
            System.out.print(num+" ");
        }
    }

    @Test
    public void testSql(){
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(0, 0, 10);
        for (DiscussPost discussPost : discussPosts) {
            System.out.println(discussPost);
        }

        discussPostMapper.selectDiscussPostRows(0);
    }

    @Test
    public void insertTestUser(){
        for(int i = 0; i < 10; i++) {
            User user = new User();
            user.setUsername("张"+ i +"三");
            user.setEmail("张"+ i*10 +"三" + "@163.com");
            user.setPassword("123456");
            int i1 = userMapper.insertUser(user);
            System.out.println(i1);
        }
    }

    @Test
    public void insertTestPost() {
        for(int i = 0; i < 10; i++) {
            DiscussPost discussPost = new DiscussPost();
            discussPost.setUserId(4);
            discussPost.setTitle(i + "今天晚上有雨");
            discussPost.setCreateTime(new Date());
            discussPost.setContent(i*100 + "具体是今天晚上有雨");
            discussPost.setStatus(0);
            discussPost.setType(0);
            int i1 = discussPostMapper.insertTestDiscussPost(discussPost);
            System.out.print(i1+" ");
        }

    }

}