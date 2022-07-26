package com.moon.coupon.controller;

import com.alibaba.fastjson.JSON;
import com.moon.coupon.entity.CouponTemplate;
import com.moon.coupon.exception.CouponException;
import com.moon.coupon.service.IBuildTemplateService;
import com.moon.coupon.service.ITemplateBaseService;
import com.moon.coupon.vo.CouponTemplateSDK;
import com.moon.coupon.vo.TemplateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Chanmoey
 * @date 2022年07月25日
 */
@Slf4j
@RestController
public class CouponTemplateController {

    private final IBuildTemplateService buildTemplateService;

    private final ITemplateBaseService templateBaseService;

    @Autowired
    public CouponTemplateController(IBuildTemplateService buildTemplateService, ITemplateBaseService templateBaseService) {
        this.buildTemplateService = buildTemplateService;
        this.templateBaseService = templateBaseService;
    }

    /**
     * 构建优惠券模板
     * localhost:7001/coupon-template/template/build
     * localhost:9000/moon/coupon-template/template/build
     * localhost:9000/moon 是访问网关
     * /coupon-template 优惠券微服务统一前缀
     */
    @PostMapping("/template/build")
    public CouponTemplate buildTemplate(@RequestBody TemplateRequest request) throws CouponException {
        log.info("Build Template: {}", JSON.toJSONString(request));
        return buildTemplateService.buildTemplate(request);
    }

    /**
     * 查询优惠券模板
     * localhost:7001/coupon-template/template/info
     * localhost:9000/moon/coupon-template/template/info
     */
    @GetMapping("/template/info")
    public CouponTemplate buildTemplateInfo(@RequestParam("id") Integer id) throws CouponException {
        log.info("Build Template Info For: {}", id);
        return templateBaseService.buildTemplateInfo(id);
    }

    /**
     * 查询所有可用的优惠券模板
     * localhost:7001/coupon-template/template/sdk/all
     * localhost:9000/moon/coupon-template/template/sdk/all
     */
    @GetMapping("/template/sdk/all")
    public List<CouponTemplateSDK> findAllUsableTemplate() {
        log.info("Find All Usable Template.");
        return templateBaseService.findAllUsableTemplate();
    }

    /**
     * 根据 ids 查询所有的 CouponTemplateSDK
     * localhost:7001/coupon-template/template/sdk/infos
     * localhost:9000/moon/coupon-template/template/sdk/infos
     */
    @GetMapping("/template/sdk/infos")
    public Map<Integer, CouponTemplateSDK> findIds2TemplateSDK(@RequestParam("ids") Collection<Integer> ids) {
        log.info("findIds2TemplateSDK: {}", ids);
        return templateBaseService.findIds2TemplateSDK(ids);
    }
}
