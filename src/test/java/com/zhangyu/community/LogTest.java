package com.zhangyu.community;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author: zhang
 * @date: 2022/3/22
 * @description:
 */

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class LogTest {


    private static final Logger logger = LoggerFactory.getLogger(LogTest.class);

    @Test
    public void logTest(){
        logger.debug("debug log");
        logger.warn("warn log");
        logger.info("info log");
        logger.error("error log");
    }
}
