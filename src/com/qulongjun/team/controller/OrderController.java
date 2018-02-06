package com.qulongjun.team.controller;

import com.jfinal.core.Controller;
import com.qulongjun.team.config.error.EmptyException;
import com.qulongjun.team.config.error.OtherException;
import com.qulongjun.team.config.error.UniqueException;
import com.qulongjun.team.domain.Order;
import com.qulongjun.team.utils.DateUtils;
import com.qulongjun.team.utils.RenderUtils;
import com.qulongjun.team.utils.UUIDUtils;
import com.sun.org.apache.xpath.internal.operations.Or;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by qulongjun on 2018/1/24.
 */
public class OrderController extends Controller {
    /**
     * 查找历史订单
     */
    public void history() {
        List<Order> orderList = Order.orderDao.find("SELECT * FROM `db_order` WHERE user_id=" + getPara("userId") + " ORDER BY order_time DESC,state DESC");
        renderJson(Order._toListJson(orderList));
    }


    /**
     * 查找今天的订单
     */
    public void today() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Order order = Order.orderDao.findFirst("SELECT * FROM `db_order` WHERE user_id=" + getPara("userId") + " AND order_time='" + sdf.format(new Date()) + "' AND state != -1");
        renderJson(order._toJson());
    }


    /**
     * 查找今天全部订单信息
     */
    public void ordered() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        List<Order> orderList = Order.orderDao.find("SELECT * FROM `db_order` WHERE order_time='" + sdf.format(new Date()) + "' AND state = 0");
        renderJson(Order._toListJson(orderList));
    }

    /**
     * 快速查看当天订单
     */
    public void index() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        List<Order> orderList = Order.orderDao.find("SELECT * FROM `db_order` WHERE order_time='" + sdf.format(new Date()) + "' AND state = 0");
        renderJson(Order._toSimpleListJson(orderList));
    }


    /**
     * 创建订单
     */
    public void create() {
        Order order = new Order();
        String time = getPara("time").split(" ")[0];
        int size = Order.orderDao.find("SELECT * FROM `db_order` WHERE user_id=" + getPara("userId") + " AND order_time='" + time + "' AND state != -1").size();
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


    /**
     * 根据ID查找
     */
    public void findById() {
        Order order = Order.orderDao.findById(getPara("id"));
        if (order != null) {
            renderJson(order._toJson());
        } else throw new EmptyException("订餐记录不存在！");
    }

    /**
     * 取消点餐
     */
    public void cancel() {
        Order order = Order.orderDao.findById(getPara("id"));
        if (order != null) {
            Boolean result = order.set("state", -1).set("cancel_time", DateUtils.getCurrentDate()).update();
            if (!result) throw new OtherException("服务器异常");
            renderJson(RenderUtils.CODE_SUCCESS);
        } else throw new EmptyException("订餐记录不存在！");
    }
}
