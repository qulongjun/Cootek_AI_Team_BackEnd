package com.qulongjun.team.config;


import com.jfinal.config.Routes;
import com.qulongjun.team.controller.FeedbackController;
import com.qulongjun.team.controller.LoginController;
import com.qulongjun.team.controller.UploadController;
import com.qulongjun.team.controller.UserController;

/**
 * 前端页面路由
 */
public class FrontRoutes extends Routes {

    @Override
    public void config() {
        this.add("/api/login", LoginController.class);
        this.add("/api/user", UserController.class);
        this.add("/api/feedback", FeedbackController.class);
        this.add("/api/upload", UploadController.class);
    }

}
