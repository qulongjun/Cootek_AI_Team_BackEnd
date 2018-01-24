package com.qulongjun.team.intercept;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.json.Jackson;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordException;
import com.jfinal.render.JsonRender;
import com.qulongjun.team.config.error.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by qulongjun on 2017/12/14.
 */
public class RequestInterceptor implements Interceptor {

    @Override
    public void intercept(Invocation inv) {
        try {
            inv.invoke();
            JsonRender render = (JsonRender) inv.getController().getRender();
            Jackson jackson = new Jackson();
            Object data = null;
            if (render.getJsonText().startsWith("[") && render.getJsonText().lastIndexOf("]") == render.getJsonText().length() - 1) {
                data = jackson.parse(render.getJsonText(), List.class);
            } else {
                data = jackson.parse(render.getJsonText(), Map.class);
            }
            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("message", "success");
            result.put("data", data);
            inv.getController().renderJson(result);
        } catch (Exception e) {
            Prop errorCode = PropKit.use("error.properties");
            Map<String, Object> _errMessage = new HashMap<>();
            _errMessage.put("code", errorCode.getInt("default"));
            _errMessage.put("message", "服务器异常");
            if (e instanceof UniqueException) {
                _errMessage.put("code", errorCode.getInt("unique"));
                _errMessage.put("message", e.getMessage());
            }
            if (e instanceof EqualException) {
                _errMessage.put("code", errorCode.getInt("equal"));
                _errMessage.put("message", e.getMessage());
            }
            if (e instanceof EmptyException) {
                _errMessage.put("code", errorCode.getInt("empty"));
                _errMessage.put("message", e.getMessage());
            }
            if (e instanceof ValidateException) {
                _errMessage.put("code", errorCode.getInt("validate"));
                _errMessage.put("message", e.getMessage());
            }
            if (e instanceof ConfirmException) {
                _errMessage.put("code", errorCode.getInt("confirm"));
                _errMessage.put("message", e.getMessage());
            }
            if (e instanceof UploadException) {
                _errMessage.put("code", errorCode.getInt("upload"));
                _errMessage.put("message", e.getMessage());
            }
            if (e instanceof OtherException) {
                _errMessage.put("code", errorCode.getInt("other"));
                _errMessage.put("message", e.getMessage());
            }

            if (e instanceof NullPointerException) {
                _errMessage.put("code", errorCode.getInt("null"));
                _errMessage.put("message", e.getMessage() != null ? e.getMessage() : "请求参数不能为空");
            }

            if (e instanceof ActiveRecordException) {
                _errMessage.put("code", errorCode.getInt("null"));
                _errMessage.put("message", e.getMessage() != null ? e.getMessage() : "数据库操作异常");
            }
            inv.getController().renderJson(_errMessage);
        }
    }
}
