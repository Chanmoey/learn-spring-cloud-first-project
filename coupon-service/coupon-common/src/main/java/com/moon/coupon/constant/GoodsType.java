package com.moon.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * 商品类型枚举
 *
 * @author Chanmoey
 * @date 2022年07月26日
 */
@Getter
@AllArgsConstructor
public enum GoodsType {

    WENYU("文娱", 1),
    SHENGXIAN("生鲜", 2),
    JIAJU("家居", 3),
    OTHERS("其他", 4),
    ALL("全品类", 5);

    private final String description;
    private final Integer code;

    public static GoodsType of(Integer code) {
        Objects.requireNonNull(code);

        return Stream.of(values())
                .filter(g -> g.code.equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(code + "no exists"));
    }
}
