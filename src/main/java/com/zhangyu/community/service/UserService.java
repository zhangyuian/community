package com.zhangyu.community.service;

import com.zhangyu.community.dao.LoginTicketMapper;
import com.zhangyu.community.dao.UserMapper;
import com.zhangyu.community.entity.LoginTicket;
import com.zhangyu.community.entity.User;
import com.zhangyu.community.utils.CommunityConstant;
import com.zhangyu.community.utils.CommunityUtils;
import com.zhangyu.community.utils.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

import static com.zhangyu.community.utils.CommunityConstant.REMEMBER_ME;

/**
 * @author: zhang
 * @date: 2022/3/22
 * @description:
 */

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${community.path.domain}")
    private String domain;

    @Autowired
    CommunityUtils communityUtils;

    @Autowired
    LoginTicketMapper loginTicketMapper;

    public User findUserById(int id) {
        return userMapper.selectById(id);
    }

    public Map<String,Object> register(User user) {
        Map<String,Object> msg = new HashMap<>();
        //判断空值
        if(user == null) {
            throw new IllegalArgumentException("参数不能为空！");
        }
        if(StringUtils.isBlank(user.getUsername())) {
            msg.put("usernameMsg", "用户名不能为空！");
            return msg;
        }
        if(StringUtils.isBlank(user.getPassword())) {
            msg.put("passwordMsg", "密码不能为空！");
            return msg;
        }
        if(StringUtils.isBlank(user.getEmail())) {
            msg.put("emailMsg", "邮箱不能为空！");
            return msg;
        }

        //验证是否存在
        if(userMapper.selectByName(user.getUsername()) != null) {
            msg.put("usernameMsg", "该用户名已经被注册！");
            return msg;
        }
        if(userMapper.selectByEmail(user.getEmail()) != null) {
            msg.put("emailMsg", "该邮箱已经被注册！");
            return msg;
        }

        //注册用户
        //先给密码加盐
        String salt = UUID.randomUUID().toString().substring(0, 5);
        String password = communityUtils.MD5(user.getPassword() + salt);
        user.setPassword(password);
        user.setSalt(salt);
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(UUID.randomUUID().toString().substring(0, 10));
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        userMapper.insertUser(user);

        //发送激活邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);
        String process = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "请激活您的账号！", process);

        return msg;
    }

    public int activation(int id, String code) {
        User user = userMapper.selectById(id);
        if(user == null) {
            return CommunityConstant.ACTIVATION_FAIL;
        }
        if(user.getActivationCode().equals(code) && user.getStatus() == 1) {
            return CommunityConstant.ACTIVATION_REPEAT;
        }
        if(user.getActivationCode().equals(code) && user.getStatus() == 0) {
            userMapper.updateStatus(id, 1);
            return CommunityConstant.ACTIVATION_SUCCESS;
        }
        return CommunityConstant.ACTIVATION_FAIL;
    }

    public Map<String, Object> login(String username, String password, int expiredSeconds) {
        Map<String, Object> map = new HashMap<>();
        // 空值处理
        if(username == null) {
            map.put("usernameMsg", "用户名不允许为空！");
            return map;
        }
        if(password == null) {
            map.put("passwordMsg", "密码不允许为空！");
            return map;
        }

        // 验证账号
        User user = userMapper.selectByName(username);
        if(user == null) {
            map.put("usernameMsg", "账号不存在！");
            return map;
        }
        if(user.getStatus() == CommunityConstant.ACTIVATION_FAIL) {
            map.put("usernameMsg", "账号未激活！");
            return map;
        }
        String password_salt = communityUtils.MD5(password + user.getSalt());
        if(!password_salt.equals(user.getPassword())){
            map.put("passwordMsg", "密码错误！");
            return map;
        }

        //生成登录令牌
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setTicket(communityUtils.generateUUID());
        loginTicket.setUserId(user.getId());
        loginTicket.setStatus(CommunityConstant.LOGIN_STATUS);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
        loginTicketMapper.insertLogin(loginTicket);
        map.put("ticket", loginTicket.getTicket());

        return map;
    }

    public Map<String, Object> logout(String ticket){
        HashMap<String, Object> msg = new HashMap<>();
        LoginTicket loginTicket = loginTicketMapper.selectByTicket(ticket);
        if(loginTicket != null) {
            loginTicketMapper.updateStatus(ticket, CommunityConstant.LOGOUT_STATUS);
            msg.put("msg", "退出成功！");
        }
        return msg;
    }
}
