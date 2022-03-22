package com.zhangyu.community.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author: zhang
 * @date: 2022/3/21
 * @description:
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private int id;
    private String username;
    private String password;
    private String salt;
    private String email;
    private int type;
    private int status;
    private String activationCode;
    private String headerUrl;
    private Date createTime;

}
