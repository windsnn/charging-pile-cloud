package com.trick.backend.common.exception;

import lombok.Getter;

@Getter
public enum DuplicateKeyEnum {
    USERNAME("uk_username", "用户名已存在"),
    PHONE("uk_phone", "手机号已存在"),
    PILE_NO("uk_pile_no", "充电桩编号已存在");

    private final String key;
    private final String message;

    DuplicateKeyEnum(String key, String message) {
        this.key = key;
        this.message = message;
    }

    // 根据异常信息查找对应的提示语
    public static String getMessageFromException(String exceptionMsg) {
        for (DuplicateKeyEnum item : DuplicateKeyEnum.values()) {
            if (exceptionMsg.contains(item.getKey())) {
                return item.getMessage();
            }
        }
        return "数据已存在";
    }
}

