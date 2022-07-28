package com.moon.coupon.feign;

import com.moon.coupon.feign.hystrix.TemplateClientHystrix;
import com.moon.coupon.vo.CommonResponse;
import com.moon.coupon.vo.CouponTemplateSDK;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 优惠券模板微服务 Feign 接口
 * FeignClient(value = "eureka-client-coupon-template", fallback = TemplateClientHystrix.class)
 * value: 需要调用的微服务的名称
 * fallback: 熔断降级策略，兜底策略
 *
 * @author Chanmoey
 * @date 2022年07月28日
 */
@FeignClient(value = "eureka-client-coupon-template", fallback = TemplateClientHystrix.class)
public interface TemplateClient {

    /**
     * 查找所有可用的优惠券模板
     */
    @GetMapping(value = "/coupon-template/template/sdk/all")
    CommonResponse<List<CouponTemplateSDK>> findAllUsableTemplate();

    /**
     * 获取模板 ids 到 CouponTemplateSDK 的映射
     */
    @GetMapping("/coupon-template/template/sdk/infos")
    CommonResponse<Map<Integer, CouponTemplateSDK>> findIds2TemplateSDK(@RequestParam("ids") Collection<Integer> ids);
}
