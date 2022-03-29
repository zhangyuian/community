package com.zhangyu.community.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author: zhang
 * @date: 2022/3/29
 * @description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private int id;
    private int fromId;
    private int toId;
    private String conversationId;
    private String content;
    private int status;
    private Date createTime;

}
