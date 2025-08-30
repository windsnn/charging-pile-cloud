package com.trick.backend.common.utils;

import java.util.Map;

public class ThreadLocalUtil {
    private static final ThreadLocal<Map<String, Object>> context = new ThreadLocal<>();

    public static Map<String, Object> getContext() {
        return context.get();
    }

    public static void setContext(Map<String, Object> map) {
        context.set(map);
    }

    public static void remove() {
        context.remove();
    }
}