package com.zhangyu.community.config;

import com.zhangyu.community.controller.interceptor.AlphaInterceptor;
import com.zhangyu.community.controller.interceptor.LoginInterceptor;
import com.zhangyu.community.controller.interceptor.LoginRequiredInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author: zhang
 * @date: 2022/3/25
 * @description:
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

//    @Autowired
//    private AlphaInterceptor alphaInterceptor;

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Autowired
    private LoginRequiredInterceptor loginRequiredInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(alphaInterceptor)
//            .excludePathPatterns("/**/*.css", "/**/*.html", "/**/*.js", "/**/*.png")
//            .addPathPatterns("/register", "/login");

        registry.addInterceptor(loginInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.html", "/**/*.js", "/**/*.png");

        registry.addInterceptor(loginRequiredInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.html", "/**/*.js", "/**/*.png");
    }
}
