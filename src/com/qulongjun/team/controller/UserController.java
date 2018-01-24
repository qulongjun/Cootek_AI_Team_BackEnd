package com.qulongjun.team.controller;

import com.jfinal.core.Controller;

import com.jfinal.json.Jackson;
import com.jfinal.kit.HttpKit;
import com.qiniu.util.Base64;
import com.qulongjun.team.config.error.OtherException;
import com.qulongjun.team.config.error.UniqueException;
import com.qulongjun.team.config.error.ValidateException;
import com.qulongjun.team.domain.User;
import com.qulongjun.team.utils.DateUtils;
import com.qulongjun.team.utils.RenderUtils;
import sun.misc.BASE64Decoder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserController extends Controller {
    /**
     * 绑定微信用户和真实信息
     */
    public void bind() {
        String key = getPara("key");
        User user = User.userDao.findFirst("SELECT * FROM `db_user` WHERE realName='" + getPara("realName") + "'");
        if (user.get("openId") != null) {
            //该用户已经绑定微信
            throw new UniqueException("无法重复绑定微信，请先解绑");
        } else {
            //绑定
            Boolean result = user.set("tel", getPara("tel")).set("openId", getPara("id")).update();
            if (!result) throw new OtherException("服务器异常");
            renderJson(RenderUtils.CODE_SUCCESS);
        }
    }

    /**
     * 获取用户信息
     */
    public void info() {
        String wx_info = HttpKit.get("https://api.weixin.qq.com/sns/jscode2session?appid=wxe435710d10ed5a2f&secret=b05a6ec806b3e21f226b17264bc3c552&js_code=" + getPara("code") + "&grant_type=authorization_code");
        if (wx_info != null && wx_info != "") {
            Jackson jackson = new Jackson();
            Map wx_user = jackson.parse(wx_info, Map.class);
            if (wx_user.get("errcode") == null) {
                //没有报错
                String openid = wx_user.get("openid").toString();
                User user = User.userDao.findFirst("SELECT * FROM `db_user` WHERE openId='" + openid+"'");
                Map result = new HashMap();
                result.put("is_bind", user != null);
                result.put("user", user);
                result.put("time", DateUtils.getCurrentDate());
                renderJson(result);
            } else {
                //获取openId错误
                throw new ValidateException("微信验证失败");
            }
        }

    }
}
