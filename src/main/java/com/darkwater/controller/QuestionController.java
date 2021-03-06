package com.darkwater.controller;

import com.darkwater.dao.CommentDao;
import com.darkwater.dao.QuestionDao;
import com.darkwater.model.*;
import com.darkwater.service.CommentService;
import com.darkwater.service.QuestionService;
import com.darkwater.service.UserService;
import com.darkwater.utils.WendaUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by lenovo1 on 2017/2/9.
 */
@Controller
public class QuestionController {
    private static final Logger LOGGER = LoggerFactory.getLogger(QuestionController.class);

    @Autowired
    QuestionDao questionDao;

    @Autowired
    QuestionService questionService;

    @Autowired
    CommentDao commentDao;

    @Autowired
    CommentService commentService;

    @Autowired
    UserService userService;

    @Autowired
    HostHolder hostHolder;

    @RequestMapping(value = {"question/add"}, method = RequestMethod.POST)
    @ResponseBody
    public String addQuestion(@RequestParam("title") String title,
                              @RequestParam("content") String content) {
        try {
            Question question = new Question();
            question.setTitle(title);
            question.setContent(content);
            question.setCommentCount(0);
            question.setCreatedDate(new Date());
            if (null == hostHolder.getUser()) {
                question.setUserId(WendaUtils.ANONYMOUS_USERID);
            } else {
                question.setUserId(hostHolder.getUser().getId());
            }
            if (questionService.addQuestion(question) > 0) {
                return WendaUtils.getJsonString(0);
            }
        } catch (Exception e) {
            LOGGER.error("增加问题失败" + e.getMessage());
        }
        return WendaUtils.getJsonString(1, "失败");
    }

    @RequestMapping(path = {"question/{questionId}"})
    public String questionDetail(Model model,
                                 @PathVariable("questionId") int questionId) {

        Question question = questionService.getQuestionById(questionId);
        Set<String> keywords = questionService.getKeyWords(question);

        List<ViewObject> vos = new ArrayList<>();
        if (null != commentDao.selectLatestCommentsByEntityId(questionId, EntityType.QUESTION,0,10)) {
            List<Comment> comments = commentDao.selectLatestCommentsByEntityId(questionId, EntityType.QUESTION,0,10);
            for (Comment comment : comments) {
                if (1 != comment.getStatus()) {
                    ViewObject vo = new ViewObject();
                    vo.set("comment", comment);
                    vo.set("user", userService.getUserById(comment.getUserId()));
                    vos.add(vo);
                }
            }
        }
        model.addAttribute("vos", vos);
        model.addAttribute("question", question);
        model.addAttribute("keywords", keywords);
        return "detail";
    }
}
