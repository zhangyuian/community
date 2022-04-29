package com.zhangyu.community.controller;

import com.zhangyu.community.entity.User;
import com.zhangyu.community.service.UserService;
import com.zhangyu.community.utils.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

/**
 * @author: zhang
 * @date: 2022/3/23
 * @description:
 */
@Controller
public class RegisterController {

    @Autowired
    UserService userService;

    @RequestMapping(path = "/register", method = RequestMethod.GET)
    public String register(Model model) {
        return "/site/register";
    }

    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public String register(Model model, User user) {
        Map<String, Object> msg = userService.register(user);
        if(msg == null || msg.size() == 0) {
            model.addAttribute("msg", "您的账号已经注册成功！");
            model.addAttribute("target", "/login");
            return "/site/operate-result";
        } else {
            model.addAttribute("usernameMsg", msg.get("usernameMsg"));
            model.addAttribute("emailMsg", msg.get("emailMsg"));
            model.addAttribute("passwordMsg", msg.get("passwordMsg"));
            return "/site/register";
        }
    }

    @RequestMapping("/activation/{id}/{code}")
    public String activation(Model model, @PathVariable int id, @PathVariable String code) {
        int activation = userService.activation(id, code);
        if(activation == CommunityConstant.ACTIVATION_FAIL) {
            model.addAttribute("msg", "激活失败！");
            model.addAttribute("target", "/index");
        } else if(activation == CommunityConstant.ACTIVATION_SUCCESS) {
            model.addAttribute("msg", "恭喜你，账号激活成功！");
            model.addAttribute("target", "/login");
        } else if(activation == CommunityConstant.ACTIVATION_REPEAT) {
            model.addAttribute("msg", "用户已激活，请不要重复激活！");
            model.addAttribute("target", "/login");
        }
        return "/site/operate-result";
    }

}
