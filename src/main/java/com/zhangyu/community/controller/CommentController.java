package com.zhangyu.community.controller;

import com.zhangyu.community.entity.Comment;
import com.zhangyu.community.service.CommentService;
import com.zhangyu.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

/**
 * @author: zhang
 * @date: 2022/3/28
 * @description:
 */
@Controller
@RequestMapping(path = "/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;


    @RequestMapping(path = "/add/{discussPostId}", method = RequestMethod.POST)
    public String addComment(@PathVariable("discussPostId") int discussPostId, Model model, Comment comment) {
        comment.setCreateTime(new Date());
        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        commentService.addComment(comment);

        return "redirect:/discuss/detail/" + discussPostId;

    }

}
