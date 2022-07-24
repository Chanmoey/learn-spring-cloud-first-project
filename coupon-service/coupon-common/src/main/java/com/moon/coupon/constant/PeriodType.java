package com.moon.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * 有效期类型
 *
 * @author Chanmoey
 * @date 2022年07月24日
 */
@Getter
@AllArgsConstructor
public enum PeriodType {

    REGULAR("固定的（固定日期）", 1),
    SHIFT("变动的（以领取之日开启计算）", 2);

    /**
     * 有效期描述。
     */
    private final String description;

    /**
     * 有效期编码
     */
    private final Integer code;

    public static PeriodType of(Integer code) {
        Objects.requireNonNull(code);
        return Stream.of(values())
                .filter(e -> e.code.equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(code + "no exists"));
    }
}
