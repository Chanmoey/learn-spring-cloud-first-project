package com.moon.coupon.feign;

import com.moon.coupon.exception.CouponException;
import com.moon.coupon.feign.hystrix.SettlementClientHystrix;
import com.moon.coupon.vo.CommonResponse;
import com.moon.coupon.vo.SettlementInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * 优惠券结算微服务 Feign 接口定义
 *
 * @author Chanmoey
 * @date 2022年07月28日
 */
@FeignClient(value = "eureka-client-coupon-settlement", fallback = SettlementClientHystrix.class)
public interface SettlementClient {

    @PostMapping(value = "/coupon-settlement/settlement/compute")
    CommonResponse<SettlementInfo> computeRule(SettlementInfo settlement) throws CouponException;
}
