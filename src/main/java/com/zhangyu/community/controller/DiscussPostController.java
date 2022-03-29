package com.zhangyu.community.controller;

import com.zhangyu.community.entity.Comment;
import com.zhangyu.community.entity.DiscussPost;
import com.zhangyu.community.entity.Page;
import com.zhangyu.community.entity.User;
import com.zhangyu.community.service.CommentService;
import com.zhangyu.community.service.DiscussPostService;
import com.zhangyu.community.service.UserService;
import com.zhangyu.community.utils.CommunityConstant;
import com.zhangyu.community.utils.CommunityUtils;
import com.zhangyu.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: zhang
 * @date: 2022/3/27
 * @description:
 */

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private CommunityUtils communityUtils;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;


    @RequestMapping(path = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title, String content) {
        User user = hostHolder.getUser();
        if(user == null) {
            return communityUtils.getJSONString(403, "请先登录！");
        }
        DiscussPost discussPost = new DiscussPost();
        discussPost.setTitle(title);
        discussPost.setContent(content);
        discussPost.setUserId(user.getId());
        discussPostService.addDiscussPost(discussPost);
        return communityUtils.getJSONString(0, "帖子发送成功！");
    }

    @RequestMapping(path = "/detail/{discussPostId}", method = RequestMethod.GET)
    public String detail(@PathVariable("discussPostId") int postId, Model model, Page page) {
        // 帖子
        DiscussPost post = discussPostService.findDiscussPost(postId);
        int userId = post.getUserId();
        // 作者
        User user = userService.findUserById(userId);
        model.addAttribute("post", post);
        model.addAttribute("user", user);

        // 设置分页信息
        page.setPath("/discuss/detail/"+postId);
        page.setLimit(5);
        page.setRows(post.getCommentCount());

        // 查找该帖子的所有评论
        List<Comment> commentList = commentService.selectCommentByEntity(ENTITY_TYPE_POST, postId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        if (commentList != null) {
            for (Comment comment : commentList) {
                // 评论VO
                HashMap<String, Object> commentVo = new HashMap<>();
                // 评论
                commentVo.put("comment", comment);
                // 作者
                commentVo.put("user", userService.findUserById(comment.getUserId()));

                // 回复(评论的评论)
                List<Comment> replyList = commentService.selectCommentByEntity(ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                for (Comment reply : replyList) {
                    HashMap<String, Object> replyVo = new HashMap<>();
                    replyVo.put("reply", reply);
                    replyVo.put("user", userService.findUserById(reply.getUserId()));
                    User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                    replyVo.put("target", target);
                    replyVoList.add(replyVo);
                }
                commentVo.put("replys", replyVoList);

                // 评论数量
                int replyCount = commentService.selectCountByEntity(comment.getEntityType(), comment.getEntityId());
                commentVo.put("replyCount", replyCount);

                commentVoList.add(commentVo);
            }
        }

        model.addAttribute("comments", commentVoList);

        return "/site/discuss-detail";

    }
}
