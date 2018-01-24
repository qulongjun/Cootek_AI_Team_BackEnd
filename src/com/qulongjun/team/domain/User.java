package com.qulongjun.team.domain;

import com.jfinal.plugin.activerecord.Model;

/**
 * Created by qulongjun on 2018/1/24.
 */
public class User extends Model<User> {
    public static User userDao = new User();
}
