package com.moon.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 规则类型枚举定义
 *
 * @author Chanmoey
 * @date 2022年07月29日
 */
@Getter
@AllArgsConstructor
public enum RuleFlag {

    // 单列表优惠券定义
    MANJIAN("满减券的计算规则"),
    ZHEKOU("折扣券的计算规则"),
    LIJIAN("立减券的计算规则"),

    // 多类别优惠券定义
    MANJIAN_ZHEKOU("满减券 + 折扣券的计算规则");

    private final String description;
}
