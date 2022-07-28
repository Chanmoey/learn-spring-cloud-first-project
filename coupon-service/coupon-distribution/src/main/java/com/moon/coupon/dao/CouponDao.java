package com.moon.coupon.dao;

import com.moon.coupon.constant.CouponStatus;
import com.moon.coupon.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author Chanmoey
 * @date 2022年07月26日
 */
public interface CouponDao extends JpaRepository<Coupon, Integer> {

    /**
     * 根据 userId + 状态 查找优惠券记录
     */
    List<Coupon> findAllByUserIdAndStatus(Long userId, CouponStatus status);
}
