package com.zhangyu.community.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.util.Map;
import java.util.UUID;

/**
 * @author: zhang
 * @date: 2022/3/23
 * @description:
 */

@Component
public class CommunityUtils {

    public static String generateUUID(){
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String MD5(String key) {
        if(StringUtils.isBlank(key)){
            return null;
        } else {
            return DigestUtils.md5DigestAsHex(key.getBytes());
        }
    }

    public static String getJSONString(int code, String msg, Map<String, Object> map) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("msg", msg);
        json.put("map", map);
        if (map != null) {
            for (String key : map.keySet()) {
                json.put(key, map.get(key));
            }
        }
        return json.toJSONString();
    }

    public static String getJSONString(int code, String msg) {
        return getJSONString(code, msg, null);
    }

    public static String getJSONString(int code) {
        return getJSONString(code, null, null);
    }
}
