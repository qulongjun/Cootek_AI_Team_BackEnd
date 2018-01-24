package com.qulongjun.team.domain;

import com.jfinal.plugin.activerecord.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by qulongjun on 2018/1/25.
 */
public class Food extends Model<Food> {
    public static Food foodDao = new Food();

    public Map _toJson() {
        Map entry = new HashMap();
        for (String key : this._getAttrNames()) {
            entry.put(key, this.get(key));
        }
        entry.put("category", FoodCategory.foodCategoryDao.findById(this.get("category_id")));
        entry.put("count", "1200");
        return entry;
    }

    public static List _toListJson(List<Food> foodList) {
        List arr = new ArrayList();
        for (Food food : foodList) {
            Map result = food._toJson();
            if (result != null) {
                arr.add(result);
            }
        }
        return arr;
    }
}
