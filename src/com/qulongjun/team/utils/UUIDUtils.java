package com.qulongjun.team.utils;

import java.util.UUID;

/**
 * Created by qulongjun on 2018/1/4.
 */
public class UUIDUtils {
    public static String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
