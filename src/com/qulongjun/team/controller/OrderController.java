package com.qulongjun.team.controller;

import com.jfinal.core.Controller;
import com.jfinal.json.Jackson;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.qulongjun.team.config.error.EmptyException;
import com.qulongjun.team.config.error.OtherException;
import com.qulongjun.team.config.error.UniqueException;
import com.qulongjun.team.config.error.ValidateException;
import com.qulongjun.team.domain.*;
import com.qulongjun.team.utils.*;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        try {
            Order order = new Order();
            String time = getPara("time").split(" ")[0];
//            if (canOrder(time + " 16:00:00")) {
            int size = Order.orderDao.find("SELECT * FROM `db_order` WHERE user_id=" + getPara("userId") + " AND order_time='" + time + "' AND state != -1").size();
            if (size == 0) {
                order.set("user_id", getPara("userId"))
                        .set("food_id", getPara("foodId"))
                        .set("create_time", DateUtils.getCurrentDate())
                        .set("order_time", time)
                        .set("state", 0);
                if (getPara("formId") != null) {
                    order.set("formId", getPara("formId"));
                }
                if (!(order.save())) throw new OtherException("服务器异常");
                renderJson(RenderUtils.CODE_SUCCESS);
            } else throw new UniqueException("当前日期已经完成订餐");
//            } else throw new UniqueException("当日订餐已截止！");
        } catch (Exception e) {
            throw new OtherException("日期格式转换错误！");
        }
    }


//    public boolean canOrder(String time) throws Exception {
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        return (simpleDateFormat.parse(time)).after(new Date());
//    }


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
        if (order != null && order.getInt("state") == 0) {
            Boolean result = order.set("state", -1).set("cancel_time", DateUtils.getCurrentDate()).update();
            if (!result) throw new OtherException("服务器异常");
            renderJson(RenderUtils.CODE_SUCCESS);
        } else throw new EmptyException("订餐记录不存在或当前状态无法取消！");
    }


    public void accept() {
        Boolean result = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                List<Order> orderList = Order.orderDao.find("SELECT * FROM `db_order` WHERE state=0 AND order_time ='" + sdf.format(new Date()) + "'");
                Boolean result = true;
                if (orderList != null) {
                    for (Order order : orderList) {
                        result = result && order.set("state", 1).set("accepter", 1).set("accept_time", DateUtils.getCurrentDate()).update();
                        if (!result) break;
                    }
                }
                return result;
            }
        });
        if (!result) throw new OtherException("服务器异常");
        renderJson(RenderUtils.CODE_SUCCESS);
    }


    public void notice() {
        String data = AjaxUtils.get("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wxe435710d10ed5a2f&secret=e666291333fc7856673fda89de1233d7");
        Jackson jackson = new Jackson();
        Map access_token = jackson.parse(data, Map.class);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        List<Order> orderList = Order.orderDao.find("SELECT * FROM `db_order` WHERE state=1 AND order_time ='" + sdf.format(new Date()) + "'");
        if (orderList != null) {
            for (Order order : orderList) {
                order.set("state", 2).set("finisher", 1).set("finish_time", DateUtils.getCurrentDate()).update();
                if (order.get("formId") != null) {
                    Food food = Food.foodDao.findById(order.get("food_id"));
                    FoodCategory category = FoodCategory.foodCategoryDao.findById(food.get("category_id"));
                    Shop shop = Shop.shopDao.findById(category.get("shop_id"));
                    User user = User.userDao.findById(order.get("user_id"));
                    Map<String, Object> params = new HashMap<>();

                    params.put("touser", user.get("openId"));
                    params.put("template_id", "ruNXLKG0gCqdabMzbaZIjVB3ftMWUkgQ5tdqnghDveM");
                    params.put("page", "pages/core/dc/dc_detail?id=" + order.get("id"));
                    params.put("form_id", order.get("formId"));
                    params.put("emphasis_keyword", "keyword3.DATA");

                    Map<String, Map<String, String>> keywords = new HashMap<>();
                    Map time = new HashMap();
                    time.put("value", order.get("order_time"));
                    time.put("color", "#7a8994");
                    keywords.put("keyword1", time);


                    Map shopMap = new HashMap();
                    shopMap.put("value", shop.get("shop"));
                    shopMap.put("color", "#7a8994");
                    keywords.put("keyword2", shopMap);

                    Map foodMap = new HashMap();
                    foodMap.put("value", food.get("food"));
                    foodMap.put("color", "#7a8994");
                    keywords.put("keyword3", foodMap);

                    Map other = new HashMap();
                    other.put("value", food.get("desp"));
                    other.put("color", "#7a8994");
                    keywords.put("keyword4", other);

                    Map place = new HashMap();
                    place.put("value", "触宝科技三楼餐厅");
                    place.put("color", "#7a8994");
                    keywords.put("keyword5", place);

                    Map name = new HashMap();
                    name.put("value", user.get("realName"));
                    name.put("color", "#7a8994");
                    keywords.put("keyword6", name);

                    Map mark = new HashMap();
                    mark.put("value", "请选择包装袋标签上姓名为\"杜欣宇\"的快餐，请勿错拿。");
                    mark.put("color", "#7a8994");
                    keywords.put("keyword7", mark);

                    params.put("data", keywords);
                    AjaxUtils.jsonPost("https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=" + access_token.get("access_token"), params);
                }
            }
        }
        renderJson(RenderUtils.CODE_SUCCESS);
    }


    /**
     * 自助下单
     */
    public void selfOrder() {
        String id = getPara("user_id"); //用户id
        String food_id = getPara("food_id"); //菜谱id
        String time = getPara("time"); //创建日期
        if (isLimit(time)) {
            int size = Order.orderDao.find("SELECT * FROM `db_order` WHERE user_id=" + id + " AND order_time='" + time + "' AND state != -1").size();
            if (size == 0) {
                //之前没点过
                Order order = new Order();
                order.set("user_id", id)
                        .set("food_id", food_id)
                        .set("create_time", DateUtils.getCurrentDate())
                        .set("order_time", time)
                        .set("state", 0);
                if (!(order.save())) throw new OtherException("服务器异常");
                renderJson(RenderUtils.CODE_SUCCESS);
            } else {
                //之前点过了
                throw new UniqueException("当前日期已经完成订餐");
            }
        } else throw new ValidateException("当前日期点餐已经截止！");
    }


    public void list() {
        String id = getPara("id");
        String time = getPara("time");
        int state = getParaToInt("state");//0 包含已取消订单，  -1 不包含已取消订单
        List<Order> orderList = Order.orderDao.find("SELECT * FROM `db_order` WHERE user_id=" + id + (state == -1 ? " AND state != -1" : "") + (time != null ? " AND order_time='" + time + "'" : "") + " ORDER BY order_time DESC,state DESC");
        renderJson(Order._toListJson(orderList));
    }

    public boolean isLimit(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            if ((sdf.parse(sdf.format(new Date()))).compareTo(sdf.parse(date)) > 0) {
                return false;
            }
            SimpleDateFormat timeDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (date.equals(sdf.format(new Date()))) {
                if ((new Date()).compareTo(timeDF.parse(date + " 23:40:00")) > 0) {
                    return false;
                } else return true;
            }
            return true;
        } catch (Exception e) {
            throw new OtherException("日期判断异常");
        }
    }
}
