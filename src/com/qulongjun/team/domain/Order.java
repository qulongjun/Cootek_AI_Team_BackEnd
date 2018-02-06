package com.qulongjun.team.domain;

import com.jfinal.plugin.activerecord.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by qulongjun on 2018/1/24.
 */
public class Order extends Model<Order> {
    public static Order orderDao = new Order();

    public Map _toJson() {
        Map entry = new HashMap();
        for (String key : this._getAttrNames()) {
            entry.put(key, this.get(key));
        }
        entry.put("food", Food.foodDao.findById(this.get("food_id"))._toJson());
        if (this.get("accepter") != null) {
            entry.put("accepter", User.userDao.findById(this.get("accepter")));

        }
        if (this.get("finisher") != null) {
            entry.put("finisher", User.userDao.findById(this.get("finisher")));
        }
        return entry;
    }

    public static List _toListJson(List<Order> orderList) {
        List arr = new ArrayList();
        for (Order order : orderList) {
            Map result = order._toJson();
            if (result != null) {
                arr.add(result);
            }
        }
        return arr;
    }


    public static Map<String, Map<String, List>> _toSimpleListJson(List<Order> orderList) {
        Map<String, Map<String, List>> results = new HashMap<>();
        for (Order order : orderList) {
            Map food = Food.foodDao.findById(order.get("food_id"))._toJson();
            User user = User.userDao.findById(order.get("user_id"));
            FoodCategory category = FoodCategory.foodCategoryDao.findById(food.get("category_id"));
            Shop shop = Shop.shopDao.findById(category.get("shop_id"));
            if (results.get(shop.getStr("shop")) != null) {
                Map<String, List> result = results.get(shop.getStr("shop"));
                if (result.get(category.getStr("category")) != null) {
                    List t = result.get(category.getStr("category"));
                    Map m = new HashMap();
                    m.put("name", food.get("food"));
                    m.put("order_id", order.get("id"));
                    m.put("user", user.get("realName"));
                    t.add(m);
                    result.put(category.getStr("category"), t);
                } else {
                    List t = new ArrayList();
                    Map m = new HashMap();
                    m.put("name", food.get("food"));
                    m.put("order_id", order.get("id"));
                    m.put("user", user.get("realName"));
                    t.add(m);
                    result.put(category.getStr("category"), t);
                }

            } else {
                Map<String, List> result = new HashMap<>();
                List t = new ArrayList();
                Map m = new HashMap();
                m.put("name", food.get("food"));
                m.put("order_id", order.get("id"));
                m.put("user", user.get("realName"));
                t.add(m);
                result.put(category.getStr("category"), t);
                results.put(shop.getStr("shop"), result);
            }
        }
        return results;
    }
}
