package com.moon.coupon.service;

import com.moon.coupon.entity.CouponTemplate;
import com.moon.coupon.exception.CouponException;
import com.moon.coupon.vo.TemplateRequest;

/**
 * 构建优惠券模板接口定义
 *
 * @author Chanmoey
 * @date 2022年07月24日
 */
public interface IBuildTemplateService {

    /**
     * 创建优惠券模板
     *
     * @param templateRequest {@link TemplateRequest} 模板请求对象
     * @return {@link CouponTemplate} 优惠券模板实体
     */
    CouponTemplate buildTemplate(TemplateRequest templateRequest) throws CouponException;
}
