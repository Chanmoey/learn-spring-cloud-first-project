package com.moon.coupon.executor.impl;

import com.moon.coupon.constant.RuleFlag;
import com.moon.coupon.executor.AbstractExecutor;
import com.moon.coupon.executor.RuleExecutor;
import com.moon.coupon.vo.CouponTemplateSDK;
import com.moon.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author Chanmoey
 * @date 2022年07月29日
 */
@Slf4j
@Component
public class ZheKouExecutor extends AbstractExecutor implements RuleExecutor {
    @Override
    public RuleFlag ruleConfig() {
        return RuleFlag.ZHEKOU;
    }

    @Override
    public SettlementInfo computeRule(SettlementInfo settlement) {
        double goodsSum = retain2Decimals(goodsCostSum(settlement.getGoodsInfos()));

        SettlementInfo probability = processGoodsTypeNotSatisfy(settlement, goodsSum);

        if (null != probability) {
            log.debug("ZheKou Coupon Template Is Not Match GoodsType!");
            return probability;
        }

        // 无门槛折扣优惠券
        CouponTemplateSDK templateSDK = settlement.getCouponAndTemplateInfos().get(0).getTemplate();

        double quota = templateSDK.getRule().getDiscount().getQuota();

        settlement.setCost(Math.max(retain2Decimals(goodsSum * (quota / 100)), minCost()));

        log.debug("Use ZheKou Coupon Make Goods Cost From {} to {}", goodsSum, settlement.getCost());

        return settlement;
    }
}
