package com.zhangyu.community.event;

import com.alibaba.fastjson.JSONObject;
import com.zhangyu.community.entity.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * @author: zhang
 * @date: 2022/4/11
 * @description:
 */

@Component
public class EventProducer {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    // 处理事件
    public void fireEvent(Event event) {
        // 将时间发布到指定的主题
        kafkaTemplate.send(event.getTopic(), JSONObject.toJSONString(event));

        //
    }

}
