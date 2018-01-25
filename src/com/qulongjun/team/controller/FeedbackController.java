package com.qulongjun.team.controller;

import com.jfinal.core.Controller;
import com.qulongjun.team.config.error.EmptyException;
import com.qulongjun.team.config.error.OtherException;
import com.qulongjun.team.domain.Feedback;
import com.qulongjun.team.domain.User;
import com.qulongjun.team.utils.DateUtils;
import com.qulongjun.team.utils.RenderUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qulongjun on 2018/1/24.
 */
public class FeedbackController extends Controller {
    /**
     * 我的问题列表
     */
    public void list() {
        List<Feedback> feedbackList = Feedback.feedbackDao.find("SELECT * FROM `db_feedback` WHERE user_id=" + getPara("id"));
        renderJson(feedbackList);
    }

    /**
     * 问题反馈
     */
    public void create() {
        User user = User.userDao.findById(getPara("id"));
        if (user != null) {
            Feedback feedback = new Feedback();
            Boolean result = feedback.set("title", getPara("title"))
                    .set("content", getPara("content"))
                    .set("user_id", user.get("id"))
                    .set("create_time", DateUtils.getCurrentDate())
                    .set("state", 0).save();
            if (!result) throw new OtherException("服务器异常");
            renderJson(RenderUtils.CODE_SUCCESS);
        } else
            throw new EmptyException("当前微信未实名认证！");
    }
}
