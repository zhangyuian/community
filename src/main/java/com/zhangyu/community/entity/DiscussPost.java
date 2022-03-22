package com.zhangyu.community.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author: zhang
 * @date: 2022/3/22
 * @description:
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiscussPost {

    private int id;
    private int userId;
    private String title;
    private String content;
    private int type;
    private int status;
    private Date createTime;
    private int commentCount;
    private double score;

}
