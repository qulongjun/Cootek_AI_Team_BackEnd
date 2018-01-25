package com.qulongjun.team.controller;

import com.jfinal.core.Controller;
import com.qulongjun.team.config.error.OtherException;
import com.qulongjun.team.config.error.UniqueException;
import com.qulongjun.team.domain.Order;
import com.qulongjun.team.utils.DateUtils;
import com.qulongjun.team.utils.RenderUtils;
import com.sun.org.apache.xpath.internal.operations.Or;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by qulongjun on 2018/1/24.
 */
public class OrderController extends Controller {
    public void history() {
        List<Order> orderList = Order.orderDao.find("SELECT * FROM `db_order` WHERE user_id=" + getPara("userId")+" ORDER BY order_time DESC");
        renderJson(Order._toListJson(orderList));
    }

    public void today() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Order order = Order.orderDao.findFirst("SELECT * FROM `db_order` WHERE user_id=" + getPara("userId") + " AND order_time='" + sdf.format(new Date()) + "'");
        renderJson(order._toJson());
    }

    public void create() {
        Order order = new Order();
        String time = getPara("time").split(" ")[0];
        int size = Order.orderDao.find("SELECT * FROM `db_order` WHERE user_id=" + getPara("userId") + " AND order_time='" + time + "'").size();
        if (size == 0) {
            Boolean result = order.set("user_id", getPara("userId"))
                    .set("food_id", getPara("foodId"))
                    .set("create_time", DateUtils.getCurrentDate())
                    .set("order_time", time)
                    .set("state", 0)
                    .save();
            if (!result) throw new OtherException("服务器异常");
            renderJson(RenderUtils.CODE_SUCCESS);
        } else throw new UniqueException("当前日期已经完成订餐");
    }
}
