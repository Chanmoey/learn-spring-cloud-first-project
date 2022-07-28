package com.moon.coupon.feign.hystrix;

import com.moon.coupon.feign.TemplateClient;
import com.moon.coupon.vo.CommonResponse;
import com.moon.coupon.vo.CouponTemplateSDK;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author Chanmoey
 * @date 2022年07月28日
 */
@Slf4j
@Component
public class TemplateClientHystrix implements TemplateClient {

    @Override
    public CommonResponse<List<CouponTemplateSDK>> findAllUsableTemplate() {
        log.error("[eureka-client-coupon-template] findAllUsableTemplate request error");
        return new CommonResponse<>(
                -1,
                "[eureka-client-coupon-template] findAllUsableTemplate request error",
                Collections.emptyList()
        );
    }

    @Override
    public CommonResponse<Map<Integer, CouponTemplateSDK>> findIds2TemplateSDK(Collection<Integer> ids) {
        log.error("[eureka-client-coupon-template] findIds2TemplateSDK request error");
        return new CommonResponse<>(
                -1,
                "[eureka-client-coupon-template] findIds2TemplateSDK request error",
                new HashMap<>()
        );
    }
}
