package com.zhangyu.community.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * @author: zhang
 * @date: 2022/3/25
 * @description:
 */
public class CookieUtils {

    public static String getValue(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if(cookie.getName().equals(name)) {
                return cookie.getValue();
            }
        }
        return "";
    }
}
