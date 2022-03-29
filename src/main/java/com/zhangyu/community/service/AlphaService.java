package com.zhangyu.community.service;

import com.zhangyu.community.dao.AlphaDao;
import com.zhangyu.community.dao.DiscussPostMapper;
import com.zhangyu.community.dao.UserMapper;
import com.zhangyu.community.entity.DiscussPost;
import com.zhangyu.community.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.PostMapping;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Date;

/**
 * @author: zhang
 * @date: 2022/3/21
 * @description:
 */

@Service
//@Scope("prototype") 一般都使用默认的singleton
public class AlphaService {

    @Autowired
    private AlphaDao alphaDao;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private TransactionTemplate transactionTemplate;

    public AlphaService() {
        System.out.println("实例化AlphaService");
    }

    @PostConstruct
    public void init() {
        System.out.println("初始化AlphaService");
    }

    @PreDestroy
    public void destory() {
        System.out.println("销毁AlphaService");
    }

    public String find() {
        return alphaDao.select();
    }


    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public void transaction() {
        User user = new User();
        user.setPassword("123");
        user.setSalt("abc");
        user.setHeaderUrl("123456");
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode("qwerty");
        user.setUsername("xixi");
        userMapper.insertUser(user);

        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(12);
        discussPost.setTitle("28今天天气很好，适合写代码!");
        discussPost.setContent("28今天写了一天的代码，卷呀卷呀你就离成功不远啦!");
        discussPost.setStatus(0);
        discussPost.setStatus(1);
        discussPost.setCreateTime(new Date());
        discussPostMapper.insertDiscussPost(discussPost);

        Integer.valueOf("abc");
    }


    public void transaction2() {
        User user = new User();
        user.setPassword("123");
        user.setSalt("abc");
        user.setHeaderUrl("123456");
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode("qwerty");
        user.setUsername("xixi123");
        userMapper.insertUser(user);

        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        transactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus status) {

                DiscussPost discussPost = new DiscussPost();
                discussPost.setUserId(12);
                discussPost.setTitle("28今天天气很好，适合写代码!");
                discussPost.setContent("28今天写了一天的代码，卷呀卷呀你就离成功不远啦!");
                discussPost.setStatus(0);
                discussPost.setStatus(1);
                discussPost.setCreateTime(new Date());
                discussPostMapper.insertDiscussPost(discussPost);

                Integer.valueOf("abc");

                return null;
            }
        });
    }


}
