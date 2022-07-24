package com.moon.coupon.converter;

import com.moon.coupon.constant.CouponCategory;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * 优惠券分类枚举属性转换器
 * AttributeConverter<X, Y>
 * X: 实体数学的类型
 * Y: 数据库字段的类型
 *
 * @author Chanmoey
 * @date 2022年07月24日
 */
@Converter
public class CouponCategoryConverter implements AttributeConverter<CouponCategory, String> {

    /**
     * 将当前实体属性X转换成数据库中的列Y
     */
    @Override
    public String convertToDatabaseColumn(CouponCategory couponCategory) {
        return couponCategory.getCode();
    }

    /**
     * 将数据库中的列Y转换成实体属性X
     */
    @Override
    public CouponCategory convertToEntityAttribute(String code) {
        return CouponCategory.of(code);
    }
}
