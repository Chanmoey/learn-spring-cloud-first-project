package com.moon.coupon.service;

import com.moon.coupon.entity.Coupon;
import com.moon.coupon.exception.CouponException;

import java.util.List;

/**
 * Redis 相关的操作服务接口定义
 *
 * @author Chanmoey
 * @date 2022年07月26日
 */
public interface IRedisService {

    /**
     * 根据 userId 和状态找到缓存的优惠券列表信息
     *
     * @param userId 用户id
     * @param status 优惠券状态 {@link com.moon.coupon.constant.CouponStatus}
     * @return {@link Coupon}s 可能返回 null，代表从没有过记录
     */
    List<Coupon> getCacheCoupon(Long userId, Integer status);

    /**
     * 保存空的优惠券列表到缓存中，避免缓存穿透
     *
     * @param userId 用户 id
     * @param status 优惠券状态 {@link com.moon.coupon.constant.CouponStatus}
     */
    void saveEmptyCouponListToCache(Long userId, List<Integer> status);

    /**
     * 尝试从 Cache 中获取一个优惠券码
     * @param templateId 优惠券模板主键
     * @return 优惠券码
     */
    String tryToAcquireCouponCadeFromCache(Integer templateId);

    /**
     * 将优惠券保存到 Cache中
     * @param userId 用户 id
     * @param coupons {@link Coupon}s
     * @param status 优惠券状态
     * @return 保存成功的个数
     * @throws CouponException 业务异常
     */
    Integer addCouponToCache(Long userId, List<Coupon> coupons, Integer status) throws CouponException;

}
