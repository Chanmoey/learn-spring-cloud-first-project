package com.moon.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * 用户优惠券状态
 *
 * @author Chanmoey
 * @date 2022年07月26日
 */
@Getter
@AllArgsConstructor
public enum CouponStatus {
    USABLE("可用的", 1),
    USED("已被用的", 2),
    EXPIRED("过期的（未被使用的）", 3);

    /**
     * 优惠券状态描述
     */
    private final String description;

    /**
     * 优惠券状态编码
     */
    private final Integer code;

    /**
     * 根据 code 返回 CouponStatus
     */
    public static CouponStatus of(Integer code) {
        Objects.requireNonNull(code);

        return Stream.of(values())
                .filter(e -> e.code.equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(code + " not exists"));
    }
}
