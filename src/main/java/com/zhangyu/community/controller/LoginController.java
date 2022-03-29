package com.zhangyu.community.controller;

import com.google.code.kaptcha.Producer;
import com.zhangyu.community.service.UserService;
import com.zhangyu.community.utils.CommunityConstant;
import com.zhangyu.community.utils.CommunityUtils;
import com.zhangyu.community.utils.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;


@Controller
public class LoginController {

    @Autowired
    Producer kaptchaProducer;

    @Autowired
    UserService userService;

    @Value("${server.servlet.context-path}") //通过配置文件注入
    private String contextPath;

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    CommunityUtils communityUtils;


    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public String login() {
        return "/site/login";
    }


    @RequestMapping(path = "/kaptcha", method = RequestMethod.GET)
    public void kaptcha(HttpServletResponse response, HttpSession session) {
        String text = kaptchaProducer.createText();
        session.setAttribute("kaptcha", text);
        BufferedImage image = kaptchaProducer.createImage(text);
        response.setContentType("image/png");
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            ImageIO.write(image, "png", outputStream);
        } catch (IOException e) {
            logger.error("验证码响应失败:" + e.getMessage());
        }
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public String login(Model model, String username, String password, String code, HttpSession session,
                        boolean rememberme, HttpServletResponse response){
        String kaptcha = (String) session.getAttribute("kaptcha");
        if(StringUtils.isBlank(code) || StringUtils.isBlank(kaptcha) || !kaptcha.equalsIgnoreCase(code)) {
            model.addAttribute("codeMsg", "验证码不正确！");
            return "/site/login";//这里返回的是网页
        }
        int expiredSeconds = rememberme ? CommunityConstant.REMEMBER_EXPIRED_SECOND : CommunityConstant.DEFAULT_EXPIRED_SECOND;
        Map<String, Object> msg = userService.login(username, password, expiredSeconds);
        if(msg.containsKey("ticket")) {
            Cookie cookie = new Cookie("ticket", msg.get("ticket").toString());
            cookie.setMaxAge(expiredSeconds);
            cookie.setPath(contextPath);
            response.addCookie(cookie);
            return "redirect:/index"; //这里重定向到/index这个Controller对应的服务上
        } else {
            model.addAttribute("usernameMsg", msg.get("usernameMsg"));
            model.addAttribute("passwordMsg", msg.get("passwordMsg"));
            return "/site/login";
        }
    }

    @RequestMapping(path = "/logout", method = RequestMethod.GET)
    public String logout(@CookieValue("ticket") String ticket) {
        Map<String, Object> msg = userService.logout(ticket);
        return "/site/login";
    }

    @RequestMapping(path = "/forget/code")
    public String sendForgetCode(Model model, String email, HttpSession session) {
        //生成验证码
        String code = communityUtils.generateUUID().substring(0, 7);
        session.setAttribute("forgetCode", code);
        session.setAttribute("email", email);
        //发送邮件
        Map<String, Object> map = userService.forget(email, code);
        //检查邮箱是否存在
        if(map.containsKey("emailMsg")){
            model.addAttribute("emailMsg", map.get("emailMsg"));
        } else {
            model.addAttribute("success", map.get("success"));
        }
        return "/site/forget";
    }

    @RequestMapping(path = "/forget", method = RequestMethod.GET)
    public String forget() {
        return "/site/forget";
    }

    @RequestMapping(value = "/forget", method = RequestMethod.POST)
    public String forget(String email, String code, String password, HttpSession session, Model model){
        //验证邮箱、密码、账号为空
        if(StringUtils.isBlank(email)) {
            model.addAttribute("emailMsg", "邮箱不能为空！");
            return "/site/forget";
        }
        if(StringUtils.isBlank(code)){
            model.addAttribute("codeMsg", "验证码不能为空！");
            return "/site/forget";
        }
        if(StringUtils.isBlank(password)){
            model.addAttribute("password", "密码不能为空！");
            return "/site/forget";
        }
        //验证code与邮箱是否正确
        if(session.getAttribute("email").equals(email) && session.getAttribute("code").equals(code)) {
            //更新密码
            userService.updatePassword(email, password);
            return "/site/login";
        } else {
            model.addAttribute("codeMsg", "验证码错误！");
            return "/site/forget";
        }
    }


}
