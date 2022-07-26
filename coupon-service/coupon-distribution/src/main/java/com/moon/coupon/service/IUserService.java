package com.moon.coupon.service;

import com.moon.coupon.entity.Coupon;
import com.moon.coupon.exception.CouponException;
import com.moon.coupon.vo.AcquireTemplateRequest;
import com.moon.coupon.vo.CouponTemplateSDK;
import com.moon.coupon.vo.SettlementInfo;

import java.util.List;

/**
 * 用户服务相关的接口定义
 * 1. 用户三类状态优惠券信息展示服务
 * 2. 查看用户当前可以领取的优惠券模板 - coupon-template 微服务配合实现
 * 3. 用户领取优惠券服务
 * 4. 用户核销优惠券服务 - coupon-settlement 微服务配合实现
 *
 * @author Chanmoey
 * @date 2022年07月26日
 */
public interface IUserService {

    /**
     * 根据用户 id 和状态查询优惠券记录
     *
     * @param userId 用户 id
     * @param status 状态
     * @return 优惠券记录列表
     * @throws CouponException 业务异常
     */
    List<Coupon> findCouponsByStatus(Long userId, Integer status) throws CouponException;

    /**
     * 根据用户 id 查找当前可以领取的优惠券信息
     *
     * @param userId 用户 id
     * @return {@link Coupon}s
     * @throws CouponException 业务异常
     */
    List<CouponTemplateSDK> findAvailableTemplate(Long userId) throws CouponException;

    /**
     * 用户领取优惠券
     *
     * @param request {@link AcquireTemplateRequest}
     * @return {@link Coupon}
     * @throws CouponException 业务异常
     */
    Coupon acquireTemplate(AcquireTemplateRequest request) throws CouponException;

    /**
     * 结算（核销）优惠券
     *
     * @param info {@link SettlementInfo}
     * @return 核销后的结算信息
     * @throws CouponException 业务异常
     */
    SettlementInfo settlement(SettlementInfo info) throws CouponException;
}
