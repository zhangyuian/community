package com.zhangyu.community.controller;

import com.zhangyu.community.entity.DiscussPost;
import com.zhangyu.community.entity.Page;
import com.zhangyu.community.entity.User;
import com.zhangyu.community.service.DiscussPostService;
import com.zhangyu.community.service.LikeService;
import com.zhangyu.community.service.UserService;
import com.zhangyu.community.utils.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: zhang
 * @date: 2022/3/22
 * @description:
 */

@Controller
public class HomeController implements CommunityConstant {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @RequestMapping(path = "/index", method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page) {
        // 方法调用前，SpringMVC会自动实例化Model 和 Page,并将Page注入Model
        // 因此，在Thymeleaf中可以直接访问Page对象中的数据
        page.setRows(discussPostService.findDiscussPostRows(0));
        System.out.println(discussPostService.findDiscussPostRows(0));
        page.setPath("/index");
        page.setLimit(10);

        List<DiscussPost> list = discussPostService.findDiscussPosts(0, page.getOffset(), page.getLimit());
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if(list != null) {
            for (DiscussPost post : list) {
                Map<String, Object> map = new HashMap<>();
                map.put("post", post);
                User user = userService.findUserById(post.getUserId());
                map.put("user", user);
                // 查询帖子点赞数量
                long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());
                map.put("likeCount", likeCount);
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts", discussPosts);
        return "/index";
    }

    @RequestMapping(path = "/error", method = RequestMethod.GET)
    public String getErrorPage() {
        return "/error/500";
    }

}
