package com.zhangyu.community.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author: zhang
 * @date: 2022/4/26
 * @description:
 */
@Configuration
@EnableScheduling
@EnableAsync
public class ThreadPoolConfig {
}
