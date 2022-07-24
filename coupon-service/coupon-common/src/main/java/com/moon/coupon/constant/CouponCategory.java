package com.moon.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * 优惠券分类
 *
 * @author Chanmoey
 * @date 2022年07月24日
 */
@Getter
@AllArgsConstructor
public enum CouponCategory {

    MANJIAN("满减券", "001"),
    ZHEKOU("折扣券", "002"),
    LIJIAN("立减券", "003");

    /**
     * 优惠券分类描述
     */
    private final String description;

    /**
     * 优惠券编码
     */
    private final String code;

    public static CouponCategory of(String code) {
        Objects.requireNonNull(code);
        return Stream.of(values())
                .filter(e -> e.code.equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(code + "no exists!"));
    }
}
