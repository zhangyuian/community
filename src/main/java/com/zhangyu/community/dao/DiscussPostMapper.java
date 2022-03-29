package com.zhangyu.community.dao;


import com.zhangyu.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {

    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);   //动态SQL

    // @Param("userId")注解用于给参数取别名
    // 如果只有一个参数，并且在<if>里使用，则必须加别名.
    int selectDiscussPostRows(@Param("userId") int userId);

    int insertDiscussPost(DiscussPost discussPost);

    DiscussPost selectDiscussPost(int postId);

    int updateCommentCount(int postId, int count);

}
