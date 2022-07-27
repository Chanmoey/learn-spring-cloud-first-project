package com.moon.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 优惠券 kafka 消息对象定义
 *
 * @author Chanmoey
 * @date 2022年07月27日
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponKafkaMessage {

    /**
     * 优惠券状态
     * 将用户的优惠券设置成这个状态
     */
    private Integer status;

    /**
     * Coupon 主键
     */
    private List<Integer> ids;
}
