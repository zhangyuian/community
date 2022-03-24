package com.zhangyu.community;

import com.zhangyu.community.utils.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.xml.transform.Templates;

/**
 * @author: zhang
 * @date: 2022/3/23
 * @description:
 */

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailSendTest {

    @Autowired
    MailClient mailClient;

    @Autowired
    TemplateEngine templateEngine;

    @Test
    public void sendEmail(){
        mailClient.sendMail("437489599@qq.com", "第三方客户端邮件发送测试", "第三方客户端邮件发送测试123");
    }

    @Test
    public void sendHtmlEmail(){
        Context context = new Context();
        context.setVariable("username", "使用html来进行测试的小伙伴！");
        String process = templateEngine.process("/mail/demo", context);
        mailClient.sendMail("437489599@qq.com", "自己的html邮件测试", process);
    }

}
