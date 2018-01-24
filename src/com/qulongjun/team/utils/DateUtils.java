package com.qulongjun.team.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by qulongjun on 2017/12/14.
 */
public class DateUtils {
    /**
     * 获取当前时间，年月日时分秒
     *
     * @return 当前时间字符串
     */
    public static String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }
}
