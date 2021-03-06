package com.qulongjun.team.domain;

import com.jfinal.plugin.activerecord.Model;

import java.util.*;

/**
 * Created by qulongjun on 2018/1/25.
 */
public class Shop extends Model<Shop> {
    public static Shop shopDao = new Shop();

    public Map _toJson() {
        Map entry = new HashMap();
        for (String key : this._getAttrNames()) {
            entry.put(key, this.get(key));
        }
        List<Food> foodList = Food.foodDao.find("SELECT f.* FROM `db_shop` s,`db_shop_category` c,`db_shop_food` f WHERE s.id=" + get("id") + " AND c.shop_id=s.id AND f.category_id=c.id AND s.state=1 AND c.state=1 AND f.state=1");
        List temp = new ArrayList();
        if (foodList != null && foodList.size() != 0) {
            for (int i = 0; i < foodList.size(); i++) {
                temp.add(foodList.get(i)._toJson());
            }
        }
        Collections.sort(temp, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                Map temp1 = (Map) o1;
                Map temp2 = (Map) o2;
                return ((Integer) temp2.get("count")) - ((Integer) temp1.get("count"));
            }
        });
        entry.put("food", temp);
        return entry;
    }


    public Map _toSimpleJson() {
        Map entry = new HashMap();
        for (String key : this._getAttrNames()) {
            entry.put(key, this.get(key));
        }
        return entry;
    }

    public static List _toListJson(List<Shop> shopList) {
        List arr = new ArrayList();
        for (Shop shop : shopList) {
            Map result = shop._toJson();
            if (result != null) {
                arr.add(result);
            }
        }
        return arr;
    }

    public static List _toListSimpleJson(List<Shop> shopList) {
        List arr = new ArrayList();
        for (Shop shop : shopList) {
            Map result = shop._toSimpleJson();
            if (result != null) {
                arr.add(result);
            }
        }
        return arr;
    }
}
