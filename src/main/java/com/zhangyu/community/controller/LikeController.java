package com.zhangyu.community.controller;

import com.zhangyu.community.entity.Event;
import com.zhangyu.community.entity.User;
import com.zhangyu.community.event.EventProducer;
import com.zhangyu.community.service.LikeService;
import com.zhangyu.community.utils.CommunityConstant;
import com.zhangyu.community.utils.CommunityUtils;
import com.zhangyu.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;

/**
 * @author: zhang
 * @date: 2022/4/1
 * @description:
 */
@Controller
public class LikeController implements CommunityConstant {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @Autowired
    private EventProducer eventProducer;

    @RequestMapping(path = "/like", method = RequestMethod.POST)
    @ResponseBody
    public String like(int entityType, int entityId, int entityUserId, int postId) {
        User user = hostHolder.getUser();
        likeService.like(user.getId(), entityType, entityId, entityUserId);

        // 获取点赞数量
        long entityLikeCount = likeService.findEntityLikeCount(entityType, entityId);

        // 获取点赞状态
        int entityLikeStatus = likeService.findEntityLikeStatus(user.getId(), entityType, entityId);

        HashMap<String, Object> map = new HashMap<>();
        map.put("likeCount", entityLikeCount);
        map.put("likeStatus", entityLikeStatus);

        // 触发点赞事件
        if (entityLikeStatus == 1) {
            Event event = new Event()
                    .setTopic(TOPIC_LIKE)
                    .setUserId(hostHolder.getUser().getId())
                    .setEntityType(entityType)
                    .setEntityId(entityId)
                    .setEntityUserId(entityUserId)
                    .setData("postId", postId);

            eventProducer.fireEvent(event);
        }

        return CommunityUtils.getJSONString(0, null, map);
    }
}
