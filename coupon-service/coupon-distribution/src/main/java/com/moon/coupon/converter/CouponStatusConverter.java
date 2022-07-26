package com.moon.coupon.converter;

import com.moon.coupon.constan.CouponStatus;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * 优惠券状态枚举转换器
 *
 * @author Chanmoey
 * @date 2022年07月26日
 */
@Converter
public class CouponStatusConverter implements AttributeConverter<CouponStatus, Integer> {
    @Override
    public Integer convertToDatabaseColumn(CouponStatus status) {
        return status.getCode();
    }

    @Override
    public CouponStatus convertToEntityAttribute(Integer integer) {
        return CouponStatus.of(integer);
    }
}
