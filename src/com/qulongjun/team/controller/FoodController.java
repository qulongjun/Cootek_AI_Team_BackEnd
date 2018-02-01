package com.qulongjun.team.controller;

import com.jfinal.core.Controller;
import com.qulongjun.team.config.error.OtherException;
import com.qulongjun.team.domain.Food;
import com.qulongjun.team.domain.Shop;
import com.qulongjun.team.utils.DateUtils;
import com.qulongjun.team.utils.RenderUtils;

import java.util.List;

/**
 * Created by qulongjun on 2018/1/25.
 */
public class FoodController extends Controller {
    /**
     * 获取商店信息
     */
    public void shop() {
        List<Shop> shopList = Shop.shopDao.find("SELECT * FROM `db_shop` WHERE state=1");
        renderJson(Shop._toListJson(shopList));
    }


    /**
     * 新增菜品
     */
    public void addFood() {
        Food food = new Food();
        Boolean result = food
                .set("food", getPara("food"))
                .set("category_id", getPara("category_id"))
                .set("state", 1)
                .set("create_time", DateUtils.getCurrentDate())
                .set("price", 30)
                .set("desp", getPara("desp")).save();
        if (!result) throw new OtherException("服务器异常");
        renderJson(RenderUtils.CODE_SUCCESS);
    }
}
