package com.moon.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 获取优惠券请求对象定义
 *
 * @author Chanmoey
 * @date 2022年07月26日
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AcquireTemplateRequest {

    private Long userId;

    private CouponTemplateSDK templateSDK;
}
