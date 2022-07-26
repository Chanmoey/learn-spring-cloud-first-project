package com.moon.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 结算信息对象定义
 * 1. userId
 * 2. 商品信息（列表）
 * 3. 优惠券列表
 * 4. 结算结果金额
 *
 * @author Chanmoey
 * @date 2022年07月27日
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SettlementInfo {

    private Long userId;

    private List<GoodsInfo> goodsInfos;

    /**
     * 优惠券列表
     */
    private List<CouponAndTemplateInfo> couponAndTemplateInfos;

    /**
     * 结果结算金额
     */
    private Double cost;

    /**
     * 是否使结算生效，即核销。
     * 因为要通过这个类的信息进行核销，同时返回核销后的结果，需要一个字段进行标记是否以及核销过。
     */
    private Boolean employ;

    /**
     * 优惠券和模板信息
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class CouponAndTemplateInfo {

        /**
         * Coupon id
         */
        private Integer id;

        private CouponTemplateSDK template;
    }
}
