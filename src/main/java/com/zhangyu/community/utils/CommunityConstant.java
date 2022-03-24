package com.zhangyu.community.utils;

public interface CommunityConstant {

    int ACTIVATION_FAIL = 0;
    int ACTIVATION_SUCCESS = 1;
    int ACTIVATION_REPEAT = 2;


    int LOGIN_STATUS = 0;
    int LOGOUT_STATUS = 1;

    int REMEMBER_ME = 0;
    int NOT_REMEMBER_ME = 1;

    int DEFAULT_EXPIRED_SECOND = 3600 * 12;
    int REMEMBER_EXPIRED_SECOND = 3600 * 24 * 30;
}
