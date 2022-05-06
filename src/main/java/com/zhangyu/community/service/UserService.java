package com.zhangyu.community.service;

import com.zhangyu.community.dao.UserMapper;
import com.zhangyu.community.entity.LoginTicket;
import com.zhangyu.community.entity.User;
import com.zhangyu.community.utils.CommunityConstant;
import com.zhangyu.community.utils.CommunityUtils;
import com.zhangyu.community.utils.MailClient;
import com.zhangyu.community.utils.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.*;

/**
 * @author: zhang
 * @date: 2022/3/22
 * @description:
 */

@Service
public class UserService implements CommunityConstant{

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
    private RedisTemplate redisTemplate;

//    @Autowired
//    LoginTicketMapper loginTicketMapper;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public User findUserById(int id) {
//        return userMapper.selectById(id);
        User user = getCache(id);
        if (user == null) {
            user = initCache(id);
        }
        return user;
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
        String password = CommunityUtils.MD5(user.getPassword() + salt);
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
        if(user.getActivationCode().equals(code) && user.getStatus() == 0) {
            return CommunityConstant.ACTIVATION_REPEAT;
        }
        if(user.getActivationCode().equals(code) && user.getStatus() == 1) {
            userMapper.updateStatus(id, 0);
            clearCache(id); // 修改user的值时，清除redis缓存
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
        String password_salt = CommunityUtils.MD5(password + user.getSalt());
        if(!password_salt.equals(user.getPassword())){
            map.put("passwordMsg", "密码错误！");
            return map;
        }

        //生成登录令牌
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setTicket(CommunityUtils.generateUUID());
        loginTicket.setUserId(user.getId());
        loginTicket.setStatus(CommunityConstant.LOGIN_STATUS);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
//        loginTicketMapper.insertLogin(loginTicket);

        // 改成用redis存储
        String ticketKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(ticketKey, loginTicket); // redis会自动将loginTicket对象序列化为json对象

        map.put("ticket", loginTicket.getTicket());
        return map;
    }

    public Map<String, Object> logout(String ticket){
        HashMap<String, Object> msg = new HashMap<>();

        String ticketKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(ticketKey);
//        LoginTicket loginTicket = loginTicketMapper.selectByTicket(ticket);
        if(loginTicket != null && loginTicket.getStatus() == CommunityConstant.LOGIN_STATUS) {
            loginTicket.setStatus(CommunityConstant.LOGOUT_STATUS);
            // 使用redis重构
            redisTemplate.opsForValue().set(ticketKey, loginTicket);
            msg.put("msg", "退出成功！");
        } else {
            msg.put("msg", "登录凭证已失效！");
        }
        return msg;
    }

    public Map<String, Object> forget(String email, String code) {
        HashMap<String, Object> map = new HashMap<>();
        User user = userMapper.selectByEmail(email);
        if(user == null) {
            map.put("emailMsg", "用户邮箱不存在！");
            return map;
        }
        int statusCode = sendForGetCode(email, code);
        if(statusCode == 0){
            map.put("success", "已发送验证码到指定邮箱中！");
        } else {
            map.put("emailMsg", "验证码发送失败！");
        }
        return map;
    }

    public int sendForGetCode(String email, String code) {
        Context context = new Context();
        context.setVariable("email", email);
        context.setVariable("code", code);
        String process = templateEngine.process("/mail/forget", context);
        try {
            mailClient.sendMail(email, "重置密码", process);
        } catch (Exception e) {
            logger.error("邮件发送失败：" + e.getMessage());
            return 1;
        }
        return 0;
    }

    public Map<String, Object> updatePassword(String email, String password) {
        HashMap<String, Object> map = new HashMap<>();
        try {
            User user = userMapper.selectByEmail(email);
            int i = userMapper.updatePassword(user.getId(), password);
        } catch (Exception e) {
            logger.error("重置密码失败：" + e.getMessage());
            map.put("msg", "重置密码失败！");
        } finally {
            return map;
        }
    }

    public LoginTicket findLoginTicket(String ticket) {
        String ticketKey = RedisKeyUtil.getTicketKey(ticket);
//        LoginTicket loginTicket = loginTicketMapper.selectByTicket(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(ticketKey);
        if(loginTicket == null || loginTicket.getStatus() == CommunityConstant.LOGOUT_STATUS || loginTicket.getExpired().before(new Date())) {
            return null;
        }
        return loginTicket;
    }

    public int updateHeaderUrl(int id, String headerUrl) {
        int res = userMapper.updateHeader(id, headerUrl);
        clearCache(id);
        return res;
    }

    public Map<String, Object> updatePassword(int userId, String oldPassword, String newPassword) {
        HashMap<String, Object> map = new HashMap<>();
        User user = userMapper.selectById(userId);
        clearCache(userId);
        if(user == null) {
            map.put("error", "用户不存在！");
            return map;
        }
        String oldPasswordSalt = CommunityUtils.MD5(oldPassword + user.getSalt());
        if(user.getPassword().equals(oldPasswordSalt)) {
            String newPasswordSalt = CommunityUtils.MD5(newPassword + user.getSalt());
            userMapper.updatePassword(user.getId(), newPasswordSalt);
            return map;
        } else {
            map.put("oldPasswordMsg", "密码输入错误！");
        }
        return map;
    }

    public User findUserByName(String username) {
        return userMapper.selectByName(username);
    }

    // 1. 优先从缓存中取值
    private User getCache(int userId) {
        String userKey = RedisKeyUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(userKey);
    }

    // 2. 取不到时初始化缓存数据
    private User initCache(int userId) {
        User user = userMapper.selectById(userId);
        String userKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(userKey, user);
        return user;
    }

    // 3. 数据变更时清除缓存数据
    private void clearCache(int userId) {
        String userKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(userKey);
    }

    public Collection<? extends GrantedAuthority> getAuthorities(int userId) {
        User user = this.findUserById(userId);

        List<GrantedAuthority> list = new ArrayList<>();
        list.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                switch (user.getType()) {
                    case 1:
                        return AUTHORITY_ADMIN;
                    case 2:
                        return AUTHORITY_MODERATOR;
                    default:
                        return AUTHORITY_USER;
                }
            }
        });
        return list;
    }

}
