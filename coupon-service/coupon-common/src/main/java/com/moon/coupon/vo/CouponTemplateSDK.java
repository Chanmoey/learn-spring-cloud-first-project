package com.moon.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 微服务之间通用的优惠券模板定义
 *
 * @author Chanmoey
 * @date 2022年07月25日
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponTemplateSDK {

    /**
     * 优惠券模板主键
     */
    private Integer id;

    private String name;

    private String logo;

    private String desc;

    private String category;

    private Integer productLine;

    private String key;

    private Integer target;

    private TemplateRule rule;
}
