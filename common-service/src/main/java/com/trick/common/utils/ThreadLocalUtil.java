package com.trick.common.utils;

import java.util.Map;

public class ThreadLocalUtil {
    private static final ThreadLocal<Map<String, Object>> context = new ThreadLocal<>();

    //获取微信用户ID
    public static Integer getUserId() {
        Object o = context.get().get("X-User-Id");
        return Integer.parseInt(o.toString());
    }

    //获取管理员ID
    public static Integer getAdminId() {
        Object o = context.get().get("X-Admin-Id");
        return Integer.parseInt(o.toString());
    }

    //获取管理员账号
    public static String getAdminUsername() {
        Object o = context.get().get("Admin-Username");
        return o.toString();
    }

    public static void setContext(Map<String, Object> map) {
        context.set(map);
    }

    public static void remove() {
        context.remove();
    }
}