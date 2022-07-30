package com.moon.coupon.executor;

import com.alibaba.fastjson2.JSON;
import com.moon.coupon.vo.GoodsInfo;
import com.moon.coupon.vo.SettlementInfo;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Chanmoey
 * @date 2022年07月29日
 */
public abstract class AbstractExecutor {

    private static final double MIN_PAY_MONEY = 0.01;

    /**
     * 校验商品类型与优惠券是否匹配
     * PS：
     * 1. 这里实现的单品类优惠券的校验，多平类优惠券需要重载此方法
     * 2. 商品只需要有一个优惠券要求的商品类型去匹配就可以
     */
    @SuppressWarnings("all")
    protected boolean isGoodsTypeSatisfy(SettlementInfo settlement) {
        List<Integer> goodsType = settlement.getGoodsInfos()
                .stream().map(GoodsInfo::getType).collect(Collectors.toList());

        // 提取优惠券中限定的商品类型，单品类优惠券
        List<Integer> templateGoodsType = JSON.parseObject(
                settlement.getCouponAndTemplateInfos().get(0).getTemplate()
                        .getRule().getUsage().getGoodsType(),
                List.class
        );

        return CollectionUtils.isNotEmpty(
                // 是否有交集
                CollectionUtils.intersection(goodsType, templateGoodsType)
        );
    }

    /**
     * 处理商品类型与优惠券限制不匹配的情况
     *
     * @param settlement {@link SettlementInfo} 用户传递的结算信息
     * @param goodsSum   商品总价（原始）
     * @return {@link SettlementInfo} 已经修改过的结算信息
     */
    protected SettlementInfo processGoodsTypeNotSatisfy(SettlementInfo settlement, double goodsSum) {
        // isGoodsTypeSatisfy 如果子类重载了，则调用子类的
        boolean isGoodsTypeSatisfy = isGoodsTypeSatisfy(settlement);

        // 商品类型不满足，直接返回商品总价，并清空优惠券
        if (!isGoodsTypeSatisfy) {
            settlement.setCost(goodsSum);
            settlement.setCouponAndTemplateInfos(Collections.emptyList());
            return settlement;
        }

        return null;
    }

    /**
     * 计算商品总价
     */
    protected double goodsCostSum(List<GoodsInfo> goodsInfos) {

        // 我觉得下面的计算方法有浮点误差
        return goodsInfos.stream().mapToDouble(g -> g.getPrice() * g.getCount()).sum();
    }

    /**
     * 保留两位小数
     */
    protected double retain2Decimals(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * 最小支付费用
     */
    protected double minCost() {
        return MIN_PAY_MONEY;
    }
}
