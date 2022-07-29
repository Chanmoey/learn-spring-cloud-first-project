package com.moon.coupon.executor;

import com.moon.coupon.constant.RuleFlag;
import com.moon.coupon.vo.SettlementInfo;

/**
 * 优惠券模板规则处理器接口定义
 *
 * @author Chanmoey
 * @date 2022年07月29日
 */
public interface RuleExecutor {

    /**
     * 规则类型标记
     *
     * @return {@link RuleFlag}
     */
    RuleFlag ruleConfig();

    /**
     * 优惠券规则的计算
     *
     * @param settlement {@link SettlementInfo} 包含了选择的优惠券信息
     * @return settlement {@link SettlementInfo} 修正过的结算信息
     */
    SettlementInfo computeRule(SettlementInfo settlement);
}
