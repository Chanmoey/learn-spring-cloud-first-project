package com.moon.coupon.service.impl;

import com.alibaba.fastjson.JSON;
import com.moon.coupon.constant.Constant;
import com.moon.coupon.constant.CouponStatus;
import com.moon.coupon.dao.CouponDao;
import com.moon.coupon.entity.Coupon;
import com.moon.coupon.exception.CouponException;
import com.moon.coupon.feign.SettlementClient;
import com.moon.coupon.feign.TemplateClient;
import com.moon.coupon.service.IRedisService;
import com.moon.coupon.service.IUserService;
import com.moon.coupon.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 所有的操作过程，状态都保存在 Redis 中，并通过 Kafka 把消息传递到 MySQL 中。
 * 之所以使用 Kafka 而不是 Spring Boot 的异步处理
 * 是因为要保证安全性，消费失败，kafka 可以重试
 *
 * @author Chanmoey
 * @date 2022年07月28日
 */
@Slf4j
@Service
public class UserServiceImpl implements IUserService {

    private final CouponDao couponDao;

    private final IRedisService redisService;

    /**
     * 模板微服务客户端
     */
    private final TemplateClient templateClient;

    /**
     * 结算微服务客户端
     */
    private final SettlementClient settlementClient;

    /**
     * Kafka 客户端
     */
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public UserServiceImpl(CouponDao couponDao, IRedisService redisService,
                           TemplateClient templateClient, SettlementClient settlementClient,
                           KafkaTemplate<String, String> kafkaTemplate) {
        this.couponDao = couponDao;
        this.redisService = redisService;
        this.templateClient = templateClient;
        this.settlementClient = settlementClient;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public List<Coupon> findCouponsByStatus(Long userId, Integer status) throws CouponException {

        // 第一次查询时，如果缓存没有记录，会插入一张无效的优惠券，但是会返回空列表。
        List<Coupon> curCached = redisService.getCachedCoupons(userId, status);
        List<Coupon> preTarget;

        // 缓存中有记录，说明用户操作过了，直接从缓存中获取。
        if (CollectionUtils.isNotEmpty(curCached)) {
            log.debug("coupon cache is not empty: {}, {}", userId, status);
            preTarget = curCached;

            // 缓存中无记录，需要从数据库中获取。
        } else {
            log.debug("coupon cache is empty, get coupon from db: {}, {}", userId, status);
            List<Coupon> dbCoupons = couponDao.findAllByUserIdAndStatus(userId, CouponStatus.of(status));
            // 如果数据库中无记录，直接返回就可以，Cache中已经加入了一张无效的优惠券。
            if (CollectionUtils.isEmpty(dbCoupons)) {
                log.debug("current user do not have coupon: {}, {}", userId, status);
                return dbCoupons;
            }

            // 填充 dbCoupons 的 templateSDK 字段
            Map<Integer, CouponTemplateSDK> id2TemplateSDK = templateClient.findIds2TemplateSDK(
                    dbCoupons.stream().map(Coupon::getTemplateId).collect(Collectors.toList())
            ).getData();

            dbCoupons.forEach(c -> c.setTemplateSDK(id2TemplateSDK.get(c.getTemplateId())));

            // 数据库中存在记录
            preTarget = dbCoupons;

            // 将记录写回 Cache （此时，缓存中有一张无效的优惠券）
            redisService.addCouponToCache(userId, preTarget, status);
        }

        // 将无效优惠券剔除
        preTarget = preTarget.stream().filter(c -> c.getId() != -1).collect(Collectors.toList());

        // 如果当前查询的是可用优惠券，需要对已过期优惠券进行延时处理
        // 如果通过消息队列来更改优惠券的状态，是不是就可以省略这一步了？
        // （就算是通过消息队列来进行优惠券状态的更改，也要进行这一步的兜底）
        if (CouponStatus.of(status) == CouponStatus.USABLE) {
            CouponClassify classify = CouponClassify.classify(preTarget);

            // 当前查询到的可用优惠券存在已过期的优惠券，需要进行延时处理
            if (CollectionUtils.isNotEmpty(classify.getExpired())) {
                log.info("Add Expired Coupons To Cache From FindCouponsByStatus: {}, {}", userId, status);
                redisService.addCouponToCache(userId, classify.getExpired(), CouponStatus.EXPIRED.getCode());
                // 发送到 kafka 中做异步处理
                kafkaTemplate.send(Constant.TOPIC,
                        JSON.toJSONString(new CouponKafkaMessage(
                                CouponStatus.EXPIRED.getCode(),
                                classify.getExpired().stream().map(Coupon::getId).collect(Collectors.toList())
                        ))
                );
            }

            return classify.getUsable();
        }
        return preTarget;
    }

    @Override
    public List<CouponTemplateSDK> findAvailableTemplate(Long userId) throws CouponException {

        long curTime = new Date().getTime();
        List<CouponTemplateSDK> templateSDKS = templateClient.findAllUsableTemplate().getData();
        log.debug("Find All Template from TemplateClient Count: {}", templateSDKS.size());

        // 过滤过期的优惠券模板
        templateSDKS = templateSDKS.stream()
                .filter(t -> t.getRule().getExpiration().getDeadline() > curTime)
                .collect(Collectors.toList());
        log.info("Find Usable Template Count: {}", templateSDKS.size());

        // key 是 TemplateId
        // value 中的 key（left） 是 template limitation, value（right） 是优惠券模板
        Map<Integer, Pair<Integer, CouponTemplateSDK>> limit2Template =
                new HashMap<>(templateSDKS.size() * 2);
        templateSDKS.forEach(t -> limit2Template.put(t.getId(), Pair.of(t.getRule().getLimitation(), t)));

        List<CouponTemplateSDK> result = new ArrayList<>(limit2Template.size());
        List<Coupon> userUsableCoupons = findCouponsByStatus(userId, CouponStatus.USABLE.getCode());

        log.debug("Current User Has Usable Coupons: {}, {}", userId, userUsableCoupons.size());

        // key 是 Template, value 是用户领取该模板的优惠券数量
        Map<Integer, List<Coupon>> templateId2Coupons = userUsableCoupons
                .stream()
                .collect(Collectors.groupingBy(Coupon::getTemplateId));

        // 根据 Template 的 Rule 判断是否可以领取优惠券模板
        limit2Template.forEach((k, v) -> {
            int limitation = v.getLeft();
            CouponTemplateSDK templateSDK = v.getRight();

            if (templateId2Coupons.containsKey(k) && templateId2Coupons.get(k).size() >= limitation) {
                return;
            }

            result.add(templateSDK);
        });

        return result;
    }

    /**
     * 用户领取优惠券
     * 1. 从 TemplateClient 拿到对应的优惠券，并检查是否过期
     * 2. 根据 limitation 判断用户是否可以领取
     * 3. save to db
     * 4. 填充 CouponTemplateSDK
     * 5. save to cache
     *
     * @param request {@link AcquireTemplateRequest}
     * @return {@link Coupon}
     */
    @Override
    public Coupon acquireTemplate(AcquireTemplateRequest request) throws CouponException {
        // 获取优惠券模板
        Map<Integer, CouponTemplateSDK> id2template = templateClient.findIds2TemplateSDK(
                Collections.singletonList(request.getTemplateSDK().getId())
        ).getData();

        // 判断优惠券模板是否存在
        if (id2template.size() <= 0) {
            log.error("Can Not Acquire Template From TemplateClient: {}", request.getTemplateSDK().getId());
            throw new CouponException("Can Not Acquire Template From TemplateClient");
        }

        // 查询用户目前自身可用的优惠券信息
        List<Coupon> userUsableCoupons = findCouponsByStatus(request.getUserId(), CouponStatus.USABLE.getCode());

        // 根据 template id 分类
        Map<Integer, List<Coupon>> templateId2Coupons = userUsableCoupons.stream()
                .collect(Collectors.groupingBy(Coupon::getTemplateId));

        // 判断是否领过优惠券，并且达到没人可领取的最大数量
        if (templateId2Coupons.containsKey(request.getTemplateSDK().getId())
                && templateId2Coupons.get(request.getTemplateSDK().getId()).size()
                >= id2template.get(request.getTemplateSDK().getId()).getRule().getLimitation()) {
            log.error("Exceed Template Assign Limitation: {}", request.getTemplateSDK().getId());
            throw new CouponException("Exceed Template Assign Limitation");
        }

        // 尝试去获取优惠券码
        String couponCode = redisService.tryToAcquireCouponCadeFromCache(request.getTemplateSDK().getId());
        if (StringUtils.isEmpty(couponCode)) {
            log.error("Can Not Acquire Coupon Code: {}", request.getTemplateSDK().getId());
            throw new CouponException("Can Not Acquire Coupon Code");
        }

        Coupon newCoupon = new Coupon(
                request.getTemplateSDK().getId(), request.getUserId(),
                couponCode, CouponStatus.USABLE
        );

        // 保存数据库
        newCoupon = couponDao.save(newCoupon);

        // 填充 Coupon 对象的 CouponTemplateSDK，一定要在放入缓存之前去填充
        newCoupon.setTemplateSDK(request.getTemplateSDK());

        redisService.addCouponToCache(request.getUserId(),
                Collections.singletonList(newCoupon),
                CouponStatus.USABLE.getCode());

        return newCoupon;
    }

    @Override
    public SettlementInfo settlement(SettlementInfo info) throws CouponException {
        return null;
    }
}
