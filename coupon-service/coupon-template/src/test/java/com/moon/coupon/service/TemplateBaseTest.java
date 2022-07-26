package com.moon.coupon.service;

import com.alibaba.fastjson.JSON;
import com.moon.coupon.constant.CouponCategory;
import com.moon.coupon.constant.DistributeTarget;
import com.moon.coupon.constant.PeriodType;
import com.moon.coupon.constant.ProductLine;
import com.moon.coupon.exception.CouponException;
import com.moon.coupon.vo.TemplateRequest;
import com.moon.coupon.vo.TemplateRule;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

/**
 * 优惠券模板基础服务测试
 *
 * @author Chanmoey
 * @date 2022年07月26日
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TemplateBaseTest {

    @Autowired
    private ITemplateBaseService templateBaseService;

    @Test
    public void testBuildTemplateInfo() throws CouponException {
        System.out.println(JSON.toJSONString(
                templateBaseService.buildTemplateInfo(12)
        ));

        System.out.println(JSON.toJSONString(
                templateBaseService.buildTemplateInfo(13)
        ));
    }

    @Test
    public void testFindAllUsableTemplate() {
        System.out.println(JSON.toJSONString(
                templateBaseService.findAllUsableTemplate()
        ));
    }

    @Test
    public void testFindIds2TemplateSKD() {
        System.out.println(JSON.toJSONString(
                templateBaseService.findIds2TemplateSDK(Arrays.asList(1, 2, 12))
        ));
    }
}
