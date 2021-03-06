package com.qulongjun.team.domain;

import com.jfinal.plugin.activerecord.Model;

import java.util.*;

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
        entry.put("category", FoodCategory.foodCategoryDao.findById(this.get("category_id"))._toJson());
        entry.put("count", Food.foodDao.find("SELECT * FROM `db_order` WHERE food_id=" + this.get("id")).size());
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
        Collections.sort(arr, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                Map temp1 = (Map) o1;
                Map temp2 = (Map) o2;
                return ((Integer)temp2.get("count")) - ((Integer)temp1.get("count"));
            }
        });
        return arr;
    }
}
