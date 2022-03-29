package com.zhangyu.community.dao;

import com.zhangyu.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {

    List<Message> selectConversations(int userId, int offset, int limit);

    int selectConversationCount(int userId);

    List<Message> selectLetters(String conversationId, int offset, int limit);

    int selectLetterCount(String conversationId);

    // 通过这个方法可以查到总的和某个会话中（conversationId = null时）
    int selectUnreadCount(int userId, String conversationId);

    // 发送私信
    int insertMessage(Message message);

    // 设置私信状态
    int updateStatus(List<Integer> ids, int status);

}
