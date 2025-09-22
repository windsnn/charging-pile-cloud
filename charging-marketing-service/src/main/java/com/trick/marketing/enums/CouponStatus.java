package com.trick.marketing.enums;

import com.trick.common.exception.BusinessException;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public enum CouponStatus {
    NOT_ENABLED(0, "未启用"),
    ENABLED(1, "已启用"),
    EXPIRED(2, "已过期");

    private static final Map<Integer, CouponStatus> CACHE =
            Arrays.stream(CouponStatus.values())
                    .collect(Collectors.toMap(CouponStatus::getStatus, e -> e));

    private final int status;
    private final String desc;

    CouponStatus(int status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public static CouponStatus of(int code) {
        CouponStatus status = CACHE.get(code);
        if (status == null) {
            throw new BusinessException("没有这个状态: " + code);
        }
        return status;
    }
}
