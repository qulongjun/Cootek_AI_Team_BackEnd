package com.qulongjun.team.domain;

import com.jfinal.plugin.activerecord.Model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by qulongjun on 2018/1/25.
 */
public class FoodCategory extends Model<FoodCategory> {
    public static FoodCategory foodCategoryDao = new FoodCategory();

    public Map _toJson() {
        Map entry = new HashMap();
        for (String key : this._getAttrNames()) {
            entry.put(key, this.get(key));
        }
        entry.put("shop", Shop.shopDao.findById(this.get("shop_id")));
        return entry;
    }
}
