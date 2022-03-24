package com.zhangyu.community.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.util.UUID;

/**
 * @author: zhang
 * @date: 2022/3/23
 * @description:
 */

@Component
public class CommunityUtils {

    public String generateUUID(){
        return UUID.randomUUID().toString().replace("-", "");
    }

    public String MD5(String key) {
        if(StringUtils.isBlank(key)){
            return null;
        } else {
            return DigestUtils.md5DigestAsHex(key.getBytes());
        }
    }
}
