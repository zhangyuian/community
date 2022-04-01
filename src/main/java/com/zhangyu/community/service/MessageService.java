package com.zhangyu.community.service;

import com.zhangyu.community.dao.MessageMapper;
import com.zhangyu.community.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: zhang
 * @date: 2022/3/29
 * @description:
 */

@Service
public class MessageService {

    @Autowired
    private MessageMapper messageMapper;

    public List<Message> findConversations(int userId, int offset, int limit) {
        return messageMapper.selectConversations(userId, offset, limit);
    }

    public int findConversationCount(int userId) {
        return messageMapper.selectConversationCount(userId);
    }

    public List<Message> findLetters(String conversationId, int offset, int limit) {
        return messageMapper.selectLetters(conversationId, offset, limit);
    }

    public int findLetterCount(String conversationId) {
        return messageMapper.selectLetterCount(conversationId);
    }

    public int findUnreadCount(int userId, String conversationId) {
        return messageMapper.selectUnreadCount(userId, conversationId);
    }

    public int addMessage(Message message) {
        return messageMapper.insertMessage(message);
    }

    // 设置已读
    public int setReadStatus(List<Integer> ids) {
        return messageMapper.updateStatus(ids, 1);
    }

    // 设置删除私信
    public int setDeleteStatus(int id) {
        ArrayList<Integer> ids = new ArrayList<>();
        ids.add(id);
        return messageMapper.updateStatus(ids, 2);
    }


}
