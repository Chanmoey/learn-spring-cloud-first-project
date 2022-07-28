package com.moon.coupon.feign.hystrix;

import com.moon.coupon.exception.CouponException;
import com.moon.coupon.feign.SettlementClient;
import com.moon.coupon.vo.CommonResponse;
import com.moon.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 结算微服务熔断策略实现
 *
 * @author Chanmoey
 * @date 2022年07月28日
 */
@Slf4j
@Component
public class SettlementClientHystrix implements SettlementClient {
    @Override
    public CommonResponse<SettlementInfo> computeRule(SettlementInfo settlement) throws CouponException {
        log.error("[eureka-client-coupon-settlement] computeRule request error");

        settlement.setEmploy(false);
        settlement.setCost(-1.0);

        return new CommonResponse<>(
                -1,
                "[eureka-client-coupon-settlement] computeRule request error",
                settlement
        );
    }
}
