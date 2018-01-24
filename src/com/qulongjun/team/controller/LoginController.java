package com.qulongjun.team.controller;

import com.jfinal.core.Controller;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by qulongjun on 2018/1/24.
 */
public class LoginController extends Controller {
    public void bind() {
        Map result = new HashMap<>();
        result.put("code",200);
        renderJson(result);
    }
}
