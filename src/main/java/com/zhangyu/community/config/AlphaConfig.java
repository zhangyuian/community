package com.zhangyu.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;

/**
 * @author: zhang
 * @date: 2022/3/21
 * @description:
 */

@Configuration
public class AlphaConfig {

    @Bean //装配第三方jar包里面的类
    public SimpleDateFormat simpleDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

}
