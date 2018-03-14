package com.qulongjun.team.controller;

import com.jfinal.core.Controller;

import com.jfinal.json.Jackson;
import com.jfinal.kit.HttpKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.qiniu.util.Base64;
import com.qulongjun.team.config.error.EmptyException;
import com.qulongjun.team.config.error.OtherException;
import com.qulongjun.team.config.error.UniqueException;
import com.qulongjun.team.config.error.ValidateException;
import com.qulongjun.team.domain.User;
import com.qulongjun.team.utils.DateUtils;
import com.qulongjun.team.utils.RenderUtils;
import sun.misc.BASE64Decoder;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class UserController extends Controller {
    /**
     * 绑定微信用户和真实信息
     */
    public void bind() {
        User newUser = User.userDao.findFirst("SELECT * FROM `db_user` WHERE realName='" + getPara("realName") + "'");
        if (newUser == null) throw new EmptyException("请输入团队成员真实姓名");
        Boolean result = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                Boolean result = true;
                Boolean change_bind = getParaToBoolean("change_bind");
                if (change_bind) {
                    //换绑
                    User oldUser = User.userDao.findFirst("SELECT * FROM `db_user` WHERE openId='" + getPara("id") + "'");
                    if (oldUser != null) {
                        result = result && oldUser.set("tel", null).set("openId", null).update();
                    }
                }
                User newUser = User.userDao.findFirst("SELECT * FROM `db_user` WHERE realName='" + getPara("realName") + "'");
                //绑定
                result = result && newUser.set("tel", getPara("tel")).set("openId", getPara("id")).update();
                return result;
            }
        });
        if (!result) throw new OtherException("服务器异常");
        renderJson(RenderUtils.CODE_SUCCESS);

    }

    /**
     * 获取用户信息
     */
    public void info() {
        String wx_info = HttpKit.get("https://api.weixin.qq.com/sns/jscode2session?appid=wxe435710d10ed5a2f&secret=e666291333fc7856673fda89de1233d7&js_code=" + getPara("code") + "&grant_type=authorization_code");
        if (wx_info != null && wx_info != "") {
            Jackson jackson = new Jackson();
            Map wx_user = jackson.parse(wx_info, Map.class);
            if (wx_user.get("errcode") == null) {
                //没有报错
                String openid = wx_user.get("openid").toString();
                User user = User.userDao.findFirst("SELECT * FROM `db_user` WHERE openId='" + openid + "'");
                Map result = new HashMap();
                result.put("is_bind", user != null);
                result.put("id", openid);
                result.put("user", user);
                result.put("time", DateUtils.getCurrentDate());
                renderJson(result);
            } else {
                //获取openId错误
                throw new ValidateException("微信验证失败");
            }
        }

    }


    /**
     * 获取我的id
     */
    public void getId() {
        String tel = getPara("tel");
        User user = User.userDao.findFirst("SELECT * FROM `db_user` WHERE tel='" + tel + "'");
        if (user != null) {
            renderJson(user);
        }
    }
}
