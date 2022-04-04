package com.zhangyu.community.controller;

import com.zhangyu.community.annotation.LoginRequired;
import com.zhangyu.community.entity.User;
import com.zhangyu.community.service.FollowService;
import com.zhangyu.community.service.LikeService;
import com.zhangyu.community.service.UserService;
import com.zhangyu.community.utils.CommunityConstant;
import com.zhangyu.community.utils.CommunityUtils;
import com.zhangyu.community.utils.HostHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Map;

/**
 * @author: zhang
 * @date: 2022/3/25
 * @description:
 */
@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant {

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${community.path.domain}")
    private String domain;

    @Autowired
    private CommunityUtils communityUtils;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;


    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @LoginRequired
    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public String upload(Model model, MultipartFile headerImage) {//MultipartFile的名字必须和表单中提交的名字相同
        if (headerImage == null) {
            model.addAttribute("error", "您还没有选择图片！");
            return "/site/setting";
        }
        String originalFilename = headerImage.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        if (!suffix.equals(".png")) {
            model.addAttribute("error", "文件的格式不正确！");
        }
        String prefix = communityUtils.generateUUID().substring(0, 7);
        String pathName = uploadPath + "/" + prefix + suffix;

        try (
                //在（）里的文件会自动关闭
                FileOutputStream fileOutputStream = new FileOutputStream(pathName);
                InputStream inputStream = headerImage.getInputStream();
        ) {
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, len);
            }
        } catch (IOException e) {
            logger.error("文件上传失败！" + e.getMessage());
            throw new RuntimeException("文件上传失败" + e);
        }

        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + prefix + suffix;
        userService.updateHeaderUrl(user.getId(), headerUrl);

        return "redirect:/index";
    }

    @LoginRequired
    @RequestMapping(path = "/header/{fileName}", method = RequestMethod.GET)
    public void header(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        String pathName = uploadPath + "/" + fileName;
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        response.setContentType("image/" + suffix);

        try (
                FileInputStream fis = new FileInputStream(pathName);
                ServletOutputStream outputStream = response.getOutputStream();
        ) {
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = fis.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
        } catch (IOException e) {
            logger.error("读取头像失败：" + e.getMessage());
        }
    }

    @LoginRequired
    @RequestMapping(path = "/setting")
    public String userSetting() {
        return "/site/setting";
    }

    @LoginRequired
    @RequestMapping(path = "/password", method = RequestMethod.POST)
    public String setPassword(Model model, String oldPassword, String newPassword, String confirmPassword) {
        User user = hostHolder.getUser();
        model.addAttribute("user", user);
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("newPasswordMsg", "两次输入的密码不一致！");
            return "/site/setting";
        }
        Map<String, Object> map = userService.updatePassword(user.getId(), oldPassword, newPassword);
        if (map.isEmpty()) {
            model.addAttribute("msg", "密码修改成功，即将进入登录页面！");
            model.addAttribute("target", "/login");
            return "/site/operate-result";
        } else {
            model.addAttribute("oldPasswordMsg", map.get("oldPasswordMsg"));
            return "/site/setting";
        }
    }

    @RequestMapping(path = "/profile/{userId}", method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId") int userId, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在！");
        }

        // 用户
        model.addAttribute("user", user);
        // 点赞数量
        int userLikeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount", userLikeCount);

        // 关注数量
        long followeeCount = followService.findFolloweeCount(userId, ENTITY_TYPE_PERSON);
        model.addAttribute("followeeCount", followeeCount);

        // 粉丝数量
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_PERSON, userId);
        model.addAttribute("followerCount", followerCount);

        // 是否关注该用户
        boolean hasFollowed = false;
        if (hostHolder.getUser() != null) {
            hasFollowed = followService.hasFollowed(hostHolder.getUser().getId(), ENTITY_TYPE_PERSON, userId);
        }
        model.addAttribute("hasFollowed", hasFollowed);

        return "/site/profile";
    }
}
