package com.moon.coupon.executor.impl;

import com.moon.coupon.constant.RuleFlag;
import com.moon.coupon.executor.AbstractExecutor;
import com.moon.coupon.executor.RuleExecutor;
import com.moon.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author Chanmoey
 * @date 2022年07月29日
 */
@Slf4j
@Component
public class ManJianZheKouExecutor extends AbstractExecutor implements RuleExecutor {
    @Override
    public RuleFlag ruleConfig() {
        return RuleFlag.MANJIAN_ZHEKOU;
    }

    @Override
    public SettlementInfo computeRule(SettlementInfo settlement) {
        return null;
    }

    @Override
    protected boolean isGoodsTypeSatisfy(SettlementInfo settlement) {
        log.debug("Check ManJian And ZheKou Is Match Or Not!");
        return super.isGoodsTypeSatisfy(settlement);
    }
}
