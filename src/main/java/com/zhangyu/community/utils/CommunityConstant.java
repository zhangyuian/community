package com.zhangyu.community.utils;

public interface CommunityConstant {

    int ACTIVATION_SUCCESS = 0;
    int ACTIVATION_FAIL = 1;
    int ACTIVATION_REPEAT = 2;


    int LOGIN_STATUS = 0;
    int LOGOUT_STATUS = 1;

    /**
     * 是否记住登录状态
     */
    int REMEMBER_ME = 0;
    int NOT_REMEMBER_ME = 1;

    /**
     * 登录凭证保存时间
     */
    int DEFAULT_EXPIRED_SECOND = 3600 * 12;
    int REMEMBER_EXPIRED_SECOND = 3600 * 24 * 30;


    /**
     * 帖子
     */
    int ENTITY_TYPE_POST = 1;

    /**
     * 评论
     */
    int ENTITY_TYPE_COMMENT = 2;

    /**
     * 评论
     */
    int ENTITY_TYPE_PERSON = 3;

    /**
     * 主题：评论
     */
    String TOPIC_COMMENT = "comment";

    /**
     * 主题：点赞
     */
    String TOPIC_LIKE = "like";

    /**
     * 主题：关注
     */
    String TOPIC_FOLLOW = "follow";

    /**
     * 主题：关注
     */
    String TOPIC_PUBLISH = "publish";

    /**
     * 主题：删帖
     */
    String TOPIC_DELETE = "delete";

    /**
     * 主题：分享
     */
    String TOPIC_SHARE = "share";

    /**
     * 系统id
     */
    int SYSTEM_USER_ID = 1;

    /**
     * 权限：普通
     */
    String AUTHORITY_USER = "USER";

    /**
     * 权限：管理员
     */
    String AUTHORITY_ADMIN = "ADMIN";

    /**
     * 权限：版主
     */
    String AUTHORITY_MODERATOR = "MODERATOR";

}
