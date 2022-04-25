package com.zhangyu.community;

import com.zhangyu.community.dao.DiscussPostMapper;
import com.zhangyu.community.entity.DiscussPost;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.ContextConfiguration;

import javax.annotation.PostConstruct;

/**
 * @author: zhang
 * @date: 2022/4/19
 * @description:
 */

//@SpringBootTest
//@ContextConfiguration(classes = CommunityApplication.class)
//public class ESTest {
//
//
//
//    @Autowired
//    DiscussPostMapper discussPostMapper;
//
//    @Autowired
//    DiscussPostRepository discussPostRepository;
//
//    @Test
//    public void queryTest(){
//        discussPostRepository.save(discussPostMapper.selectDiscussPost(328));
//        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(328, 0, 5));
//    }
//
//}
