package com.zhangyu.community.dao;

import com.zhangyu.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {

    int selectCountByEntity(int entityType, int entityId);

    List<Comment> selectCommentByEntity(int entityType, int entityId, int offset, int limit);

    int insertComment(Comment comment);

    Comment selectCommentById(int id);

}
