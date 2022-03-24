package com.zhangyu.community.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author: zhang
 * @date: 2022/3/24
 * @description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginTicket {
    private int id;
    private int userId;
    private String ticket;
    private int status;
    private Date expired;
}
