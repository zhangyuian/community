package com.zhangyu.community.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;


import java.util.Date;

/**
 * @author: zhang
 * @date: 2022/3/22
 * @description:
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
//@Document(indexName = "discusspost", indexStoreType = "_doc", shards = 6, replicas = 3)
public class DiscussPost {

    //@Id
    private int id;
   // @Field(type = FieldType.Integer)
    private int userId;
    //@Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String title;
    //@Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String content;
    //@Field(type = FieldType.Integer)
    private int type;
    //@Field(type = FieldType.Integer)
    private int status;
    //@Field(type = FieldType.Date)
    private Date createTime;
    //@Field(type = FieldType.Integer)
    private int commentCount;
    //@Field(type = FieldType.Double)
    private double score;

}
