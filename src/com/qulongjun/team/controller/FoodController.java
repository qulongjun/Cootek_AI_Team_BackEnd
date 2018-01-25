package com.qulongjun.team.controller;

import com.jfinal.core.Controller;
import com.qulongjun.team.domain.Shop;

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
}
