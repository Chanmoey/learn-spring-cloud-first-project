package com.moon.coupon.service;

import com.moon.coupon.entity.CouponTemplate;

/**
 * 异步服务接口定义
 *
 * @author Chanmoey
 * @date 2022年07月25日
 */
public interface IAsyncService {

    /**
     * 根据模板异步地创建优惠券码
     * @param couponTemplate {@link CouponTemplate} 优惠券模板实体
     */
    void asyncConstructCouponByTemplate(CouponTemplate couponTemplate);
}
