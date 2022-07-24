package com.moon.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * 产品线枚举
 *
 * @author Chanmoey
 * @date 2022年07月24日
 */
@Getter
@AllArgsConstructor
public enum ProductLine {

    DAMAO("大猫", 1),
    DABAO("大宝", 2);

    /**
     * 产品线描述
     */
    private final String description;

    /**
     * 产品线编码
     */
    private final Integer code;

    public static ProductLine of(Integer code) {
        Objects.requireNonNull(code);
        return Stream.of(values())
                .filter(e -> e.code.equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(code + "no exists"));
    }
}
