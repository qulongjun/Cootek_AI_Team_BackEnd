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
}
