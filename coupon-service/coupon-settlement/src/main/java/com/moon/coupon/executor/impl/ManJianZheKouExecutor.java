package com.moon.coupon.executor.impl;

import com.alibaba.fastjson2.JSON;
import com.moon.coupon.constant.CouponCategory;
import com.moon.coupon.constant.RuleFlag;
import com.moon.coupon.executor.AbstractExecutor;
import com.moon.coupon.executor.RuleExecutor;
import com.moon.coupon.vo.GoodsInfo;
import com.moon.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
        double goodsSum = retain2Decimals(
                goodsCostSum(settlement.getGoodsInfos())
        );

        SettlementInfo probability = processGoodsTypeNotSatisfy(
                settlement, goodsSum
        );

        if (probability != null) {
            log.debug("ManJian And ZheKou Template Is Not Match To GoodsType!");
            return probability;
        }

        SettlementInfo.CouponAndTemplateInfo manJian = null;
        SettlementInfo.CouponAndTemplateInfo zheKou = null;

        for (SettlementInfo.CouponAndTemplateInfo ct : settlement.getCouponAndTemplateInfos()) {
            if (CouponCategory.of(ct.getTemplate().getCategory()) == CouponCategory.MANJIAN) {
                manJian = ct;
            } else {
                zheKou = ct;
            }
        }

        assert manJian != null && zheKou != null;

        // 当前的优惠券和满减券不能共用（一起用），则清空优惠券
        if (!isTemplateCanShared(manJian, zheKou)) {
            log.debug("Current ManJian And ZheKou Can Not Shared!");
            settlement.setCost(goodsSum);
            settlement.setCouponAndTemplateInfos(Collections.emptyList());
            return settlement;
        }

        // 真正结算
        List<SettlementInfo.CouponAndTemplateInfo> ctInfos = new ArrayList<>();
        double manJianBase = manJian.getTemplate().getRule().getDiscount().getBase();
        double manJianQuota = manJian.getTemplate().getRule().getDiscount().getQuota();

        // 最终的价格
        double targetSum = goodsSum;

        // 计算满减
        if (targetSum >= manJianBase) {
            targetSum -= manJianQuota;
            ctInfos.add(manJian);
        }

        // 计算折扣
        double zheKouQuota = zheKou.getTemplate().getRule().getDiscount().getQuota();
        targetSum *= zheKouQuota / 100;
        ctInfos.add(zheKou);

        settlement.setCouponAndTemplateInfos(ctInfos);
        settlement.setCost(retain2Decimals(Math.max(targetSum, minCost())));

        log.debug("Use ManJian And ZheKou Coupon Make Goods From {} To {}", goodsSum, settlement.getCost());
        return settlement;
    }

    /**
     * 判断两个优惠券能否共用
     */
    @SuppressWarnings("all")
    private boolean isTemplateCanShared(SettlementInfo.CouponAndTemplateInfo manjian,
                                        SettlementInfo.CouponAndTemplateInfo zhekou) {
        String manjianKey = manjian.getTemplate().getKey()
                + String.format("%04d", manjian.getTemplate().getId());
        String zhekouKey = zhekou.getTemplate().getKey()
                + String.format("%04d", zhekou.getTemplate().getId());

        List<String> allSharedKeysForManJian = new ArrayList<>();
        allSharedKeysForManJian.add(manjianKey);
        allSharedKeysForManJian.addAll(JSON.parseObject(
                manjian.getTemplate().getRule().getWeight(),
                List.class)
        );

        List<String> allSharedKeysForZheKou = new ArrayList<>();
        allSharedKeysForZheKou.add(zhekouKey);
        allSharedKeysForZheKou.addAll(JSON.parseObject(
                zhekou.getTemplate().getRule().getWeight(),
                List.class
        ));

        return CollectionUtils.isSubCollection(
                Arrays.asList(manjianKey, zhekouKey), allSharedKeysForManJian)
                || CollectionUtils.isSubCollection(
                Arrays.asList(manjianKey, zhekouKey), allSharedKeysForZheKou);
    }

    /**
     * 校验商品类型与优惠券是否匹配
     * PS：
     * 1. 实现满减+折扣优惠券的校验
     * 2. 如果想要使用多类优惠券，则必须所有商品都包含在内，即差集为空
     */
    @Override
    @SuppressWarnings("all")
    protected boolean isGoodsTypeSatisfy(SettlementInfo settlement) {
        log.debug("Check ManJian And ZheKou Is Match Or Not!");

        // 获取订单商品的类型
        List<Integer> goodsType = settlement.getGoodsInfos().stream()
                .map(GoodsInfo::getType).collect(Collectors.toList());

        // 获取优惠券支持商品的类型
        List<Integer> templateGoodsType = new ArrayList<>();
        settlement.getCouponAndTemplateInfos().forEach(ct ->
                templateGoodsType.addAll(JSON.parseObject(
                        ct.getTemplate().getRule().getUsage().getGoodsType(),
                        List.class
                )));

        // 如果想要使用多类优惠券，则必须所有商品都包含在内，即差集为空
        return CollectionUtils.isEmpty(CollectionUtils.subtract(
                goodsType, templateGoodsType
        ));
    }
}
