package com.qulongjun.team.config;


import com.jfinal.config.Routes;
import com.qulongjun.team.controller.*;

/**
 * 前端页面路由
 */
public class FrontRoutes extends Routes {

    @Override
    public void config() {
        this.add("/api/login", LoginController.class);
        this.add("/api/user", UserController.class);
        this.add("/api/feedback", FeedbackController.class);
        this.add("/api/order", OrderController.class);
        this.add("/api/food", FoodController.class);

        this.add("/api/upload", UploadController.class);
    }

}
