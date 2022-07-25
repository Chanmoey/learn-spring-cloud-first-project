package com.moon.coupon.service.impl;

import com.google.common.base.Stopwatch;
import com.moon.coupon.constant.Constant;
import com.moon.coupon.dao.CouponTemplateDao;
import com.moon.coupon.entity.CouponTemplate;
import com.moon.coupon.service.IAsyncService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 异步服务接口实现
 *
 * @author Chanmoey
 * @date 2022年07月25日
 */
@Slf4j
@Service
public class AsyncServiceImpl implements IAsyncService {

    private final CouponTemplateDao templateDao;

    private final StringRedisTemplate redisTemplate;

    @Autowired
    public AsyncServiceImpl(CouponTemplateDao templateDao, StringRedisTemplate redisTemplate) {
        this.templateDao = templateDao;
        this.redisTemplate = redisTemplate;
    }

    @Async("getAsyncExecutor")
    @Override
    public void asyncConstructCouponByTemplate(CouponTemplate template) {
        Stopwatch watch = Stopwatch.createStarted();

        Set<String> couponCodes = buildCouponCode(template);

        String redisKey = String.format("%s%s",
                Constant.RedisPrefix.COUPON_TEMPLATE, template.getId().toString());
        log.info("Push Coupon Code To Redis: {}",
                redisTemplate.opsForList().rightPushAll(redisKey, couponCodes));
        template.setAvailable(true);
        templateDao.save(template);
        watch.stop();
        log.info("Construct CouponCode By Template Cost: {}ms", watch.elapsed(TimeUnit.MILLISECONDS));

        // 异步任务，可以选择发送邮件或短息等通知运营人员，优惠券已经生效。
        log.info("CouponTemplate({}) Is Available!", template.getId());
    }

    /**
     * 构建优惠券码
     * 优惠券码（对应于每一张优惠券，18位）
     * 前四位：产品线+类型
     * 中间六位：日期随机（190101）
     * 后八位：0~9的随机数构成
     *
     * @param template {@link CouponTemplate} 实体类
     * @return Set<String> 与 template.count 相同个数的优惠券码
     */
    private Set<String> buildCouponCode(CouponTemplate template) {

        // 记录方法执行的时间
        Stopwatch watch = Stopwatch.createStarted();
        Set<String> result = new HashSet<>(template.getCount());

        String prefix4 = template.getProductLine().getCode().toString()
                + template.getCategory().getCode();
        String date = new SimpleDateFormat("yyMMdd").format(template.getCreateTime());

        for (int i = 0; i < template.getCount(); i++) {
            result.add(prefix4 + buildCouponCodeSuffix14(date));
        }

        // 后14位可能会重复，所有要执行下面的
        while (result.size() < template.getCount()) {
            result.add(prefix4 + buildCouponCodeSuffix14(date));
        }

        assert result.size() == template.getCount();
        watch.stop();
        log.info("Build Coupon Code Cost: {}ms", watch.elapsed(TimeUnit.MILLISECONDS));

        return result;
    }

    /**
     * 构造优惠券码的后14位
     *
     * @param date 优惠券模板的创建日期
     * @return 14 位优惠券码
     */
    private String buildCouponCodeSuffix14(String date) {
        // 中间6位
        List<Character> chars = date.chars()
                .mapToObj(e -> (char) e).collect(Collectors.toList());
        Collections.shuffle(chars);
        String mid6 = chars.stream()
                .map(Objects::toString).collect(Collectors.joining());

        // 后8位随机数
        String suffix8 = RandomStringUtils.random(1, Constant.getNumberNoZero())
                + RandomStringUtils.randomNumeric(7);

        return mid6 + suffix8;
    }
}
