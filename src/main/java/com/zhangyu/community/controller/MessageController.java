package com.zhangyu.community.controller;

import com.zhangyu.community.entity.Message;
import com.zhangyu.community.entity.Page;
import com.zhangyu.community.entity.User;
import com.zhangyu.community.service.MessageService;
import com.zhangyu.community.service.UserService;
import com.zhangyu.community.utils.CommunityUtils;
import com.zhangyu.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * @author: zhang
 * @date: 2022/3/29
 * @description:
 */
@Controller
public class MessageController {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @RequestMapping(path = "/letter/list", method = RequestMethod.GET)
    public String getLetterList(Model model, Page page) {
        User user = hostHolder.getUser();
        // 分页信息
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRows(messageService.findConversationCount(user.getId()));

        // 私信信息装到model里
        List<Message> conversationList = messageService.findConversations(user.getId(), page.getOffset(), page.getLimit());
        List<Map<String, Object>> conversations = new ArrayList<>();
        if (conversationList != null) {
            for (Message message : conversationList) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("conversation", message);
                map.put("letterCount", messageService.findLetterCount(message.getConversationId()));
                // 该会话中未读的消息数量
                map.put("unreadCount", messageService.findUnreadCount(user.getId(), message.getConversationId()));
                // target为和你联系的用户，如果当前发送用户是你，则收到用户是target，反之就是发送用户就是target
                // 用targetId查询到用户，该目的主要是获取和你聊天用户的头像
                int targetId = user.getId() == message.getFromId() ? message.getToId() : message.getFromId();
                map.put("target", userService.findUserById(targetId));

                conversations.add(map);
            }
        }
        model.addAttribute("conversations", conversations);

        // 总共的未读消息数
        int unreadCount = messageService.findUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount", unreadCount);

        return "/site/letter";
    }

    public List<Integer> getNoReadMessageId(List<Message> messages) {
        List<Integer> ids = new ArrayList<>();
        for (Message message : messages) {
            if (message.getStatus() == 0) {
                ids.add(message.getId());
            }
        }
        return ids;
    }

    @RequestMapping(path = "/letter/detail/{conversationId}")
    public String getLetterDetail(@PathVariable("conversationId") String conversationId, Model model, Page page) {
        User user = hostHolder.getUser();

        page.setLimit(5);
        page.setPath("/letter/detail/" + conversationId);
        page.setRows(messageService.findUnreadCount(user.getId(), conversationId));

        // 私信列表
        List<Message> letterList = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> letters = new ArrayList<>();
        for (Message message : letterList) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("letter", message);
            map.put("fromUser", userService.findUserById(message.getFromId()));
            letters.add(map);
        }
        model.addAttribute("letters", letters);

        // 设置消息已读
        List<Integer> noReadMessageId = getNoReadMessageId(letterList);
        if (!noReadMessageId.isEmpty()) {
            messageService.setReadStatus(noReadMessageId);
        }

        // 通过conversationId获取target
        model.addAttribute("target", getLetterTarget(conversationId));

        return "/site/letter-detail";
    }

    public User getLetterTarget(String conversationId) {
        User user = hostHolder.getUser();
        String[] s = conversationId.split("_");
        int id0 = Integer.parseInt(s[0]);
        int id1 = Integer.parseInt(s[1]);

        if (user.getId() == id0) {
            return userService.findUserById(id1);
        } else {
            return userService.findUserById(id0);
        }
    }

    // 异步请求，所以返回Json数据
    @RequestMapping(path = "/letter/send", method = RequestMethod.POST)
    @ResponseBody
    public String sendLetter(String toName, String content) {
        User target = userService.findUserByName(toName);
        if (target == null) {
            return CommunityUtils.getJSONString(1, "该用户不存在！");
        }

        Message message = new Message();
        message.setContent(content);
        message.setFromId(hostHolder.getUser().getId());
        message.setToId(target.getId());
        if (message.getToId() < message.getFromId()) {
            message.setConversationId(message.getToId() + "_" + message.getFromId());
        } else {
            message.setConversationId(message.getFromId() + "_" + message.getToId());
        }
        message.setStatus(0);
        message.setCreateTime(new Date());
        messageService.addMessage(message);

        return CommunityUtils.getJSONString(0);
    }
}
