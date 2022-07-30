package com.moon.coupon.controller;

import com.alibaba.fastjson.JSON;
import com.moon.coupon.exception.CouponException;
import com.moon.coupon.executor.ExecuteManager;
import com.moon.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 结算服务的 Controller
 * @author Chanmoey
 * @date 2022年07月30日
 */
@Slf4j
@RestController
public class SettlementController {

    private final ExecuteManager executeManager;

    @Autowired
    public SettlementController(ExecuteManager executeManager) {
        this.executeManager = executeManager;
    }

    @PostMapping("/settlement/compute")
    public SettlementInfo computeRule(@RequestBody SettlementInfo settlement) throws CouponException {
        log.info("settlement: {}", JSON.toJSONString(settlement));
        return executeManager.couputRule(settlement);
    }
}
