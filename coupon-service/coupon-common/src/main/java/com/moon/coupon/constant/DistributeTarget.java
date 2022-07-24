package com.moon.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author Chanmoey
 * @date 2022年07月24日
 */
@Getter
@AllArgsConstructor
public enum DistributeTarget {

    SINGLE("单用户", 1),
    MULTI("多用户", 2);

    /**
     * 分发目标描述，单用户：需要用户自己去领取。多用户：系统统一进行分发。
     */
    private final String description;

    /**
     * 分发目标编码
     */
    private final Integer code;

    public static DistributeTarget of(Integer code) {
        Objects.requireNonNull(code);
        return Stream.of(values())
                .filter(e -> e.code.equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(code + "no exists"));
    }
}
