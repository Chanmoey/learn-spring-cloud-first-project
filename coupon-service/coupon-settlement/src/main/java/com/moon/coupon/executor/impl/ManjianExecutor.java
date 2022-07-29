package com.moon.coupon.executor.impl;

import com.moon.coupon.constant.RuleFlag;
import com.moon.coupon.executor.AbstractExecutor;
import com.moon.coupon.executor.RuleExecutor;
import com.moon.coupon.vo.CouponTemplateSDK;
import com.moon.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * 满减优惠券结算规则执行器
 *
 * @author Chanmoey
 * @date 2022年07月29日
 */
@Slf4j
@Component
public class ManjianExecutor extends AbstractExecutor implements RuleExecutor {
    @Override
    public RuleFlag ruleConfig() {
        return RuleFlag.MANJIAN;
    }

    @Override
    public SettlementInfo computeRule(SettlementInfo settlement) {

        // 获取商品总价
        double goodsSum = retain2Decimals(goodsCostSum(settlement.getGoodsInfos()));

        // 判断商品类型是否满足
        SettlementInfo probability = processGoodsTypeNotSatisfy(
                settlement, goodsSum
        );

        if (null != probability) {
            log.debug("ManJian Template Is Not Match To GoodsType!");
            return probability;
        }

        // 判断总价是否满足满减标准
        CouponTemplateSDK templateSDK = settlement.getCouponAndTemplateInfos()
                .get(0).getTemplate();
        double base = templateSDK.getRule().getDiscount().getBase();
        double quota = templateSDK.getRule().getDiscount().getQuota();

        // 如果不符合标准，则直接返回商品总价
        if (goodsSum < base) {
            log.debug("Current Goods Cost Sum < ManJian Coupon Base!");
            settlement.setCost(goodsSum);
            settlement.setCouponAndTemplateInfos(Collections.emptyList());
            return settlement;
        }

        settlement.setCost(retain2Decimals((
                Math.max((goodsSum - quota), minCost())
        )));
        log.debug("Use ManJian Coupon Make Goods Cost From {} To {}", goodsSum, settlement.getCost());

        return settlement;
    }
}
