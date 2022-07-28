package com.moon.coupon.service.impl;

import com.alibaba.fastjson.JSON;
import com.moon.coupon.constant.Constant;
import com.moon.coupon.constant.CouponStatus;
import com.moon.coupon.entity.Coupon;
import com.moon.coupon.exception.CouponException;
import com.moon.coupon.service.IRedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Redis相关的服务接口实现
 *
 * @author Chanmoey
 * @date 2022年07月27日
 */
@Slf4j
@Service
public class RedisServiceImpl implements IRedisService {

    private final StringRedisTemplate redisTemplate;

    @Autowired
    public RedisServiceImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    /**
     * 根据 userId 和状态找到缓存的优惠券列表数据
     *
     * @param userId 用户id
     * @param status 优惠券状态 {@link com.moon.coupon.constant.CouponStatus}
     * @return 查询结果
     */
    @Override
    @SuppressWarnings("all")
    public List<Coupon> getCachedCoupons(Long userId, Integer status) {
        log.info("Get coupons From Cache: {}, {}", userId, status);
        String redisKey = status2RedisKey(status, userId);
        List<String> couponStrings = redisTemplate.opsForHash().values(redisKey)
                .stream()
                .map(o -> Objects.toString(o, null))
                .collect(Collectors.toList());

        // 第一次，什么也没有做，就查询。其实可以考虑返回无效的 Coupon，这里返回的是空列表。
        if (CollectionUtils.isEmpty(couponStrings)) {
            saveEmptyCouponListToCache(userId, Collections.singletonList(status));
            return Collections.emptyList();
        }

        return couponStrings.stream()
                .map(cs -> JSON.parseObject(cs, Coupon.class))
                .collect(Collectors.toList());
    }

    /**
     * 保存空的优惠券列表到缓存中
     * 目的：避免缓存穿透
     *
     * @param userId 用户 id
     * @param status 优惠券状态 {@link com.moon.coupon.constant.CouponStatus}
     */
    @Override
    @SuppressWarnings("all")
    public void saveEmptyCouponListToCache(Long userId, List<Integer> status) {
        log.info("Save Empty List To Cache For User: {}, Status: {}", userId, JSON.toJSONString(status));

        // key 是 coupon_id, value 是序列化的 Coupon
        Map<String, String> invalidCouponMap = new HashMap<>();
        invalidCouponMap.put("-1", JSON.toJSONString(Coupon.invalidCoupon()));

        /**
         * 用户youhuiq
         * KV
         * K: status -> redisKey（包含 userId），所以是以（用户+状态）作为 key
         * V: {coupon_id: 序列化的 coupon}
         */
        // 使用 SessionCallback 把数据命令放入到 Redis 的 pipeline
        SessionCallback<Object> sessionCallback = new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                status.forEach(s -> {
                    String redisKey = status2RedisKey(s, userId);
                    redisOperations.opsForHash().putAll(redisKey, invalidCouponMap);
                });

                return null;
            }
        };

        log.info("Pipeline Exe Result: {}", JSON.toJSONString(redisTemplate.executePipelined(sessionCallback)));
    }

    /**
     * 从 Redis 中根据优惠券模板 id 获取优惠券码
     *
     * @param templateId 优惠券模板主键
     * @return 优惠券码
     */
    @Override
    public String tryToAcquireCouponCadeFromCache(Integer templateId) {
        String redisKey = String.format("%s%s", Constant.RedisPrefix.COUPON_TEMPLATE, templateId.toString());
        String couponCode = redisTemplate.opsForList().leftPop(redisKey);

        log.info("Acquire Coupon Code: {}, {}, {}", templateId, redisKey, couponCode);

        return couponCode;
    }

    /**
     * 将用户优惠券信息添加到 Redis
     *
     * @param userId  用户 id
     * @param coupons {@link Coupon}s
     * @param status  优惠券状态
     * @return 成功数目
     * @throws CouponException 业务异常
     */
    @Override
    public Integer addCouponToCache(Long userId, List<Coupon> coupons, Integer status) throws CouponException {
        log.info("Add Coupon To Cache: {}, {}, {}", userId, JSON.toJSONString(coupons), status);

        Integer result = -1;
        CouponStatus couponStatus = CouponStatus.of(status);

        switch (couponStatus) {
            case USABLE:
                result = addCouponToCacheForUsable(userId, coupons);
                break;
            case USED:
                result = addCouponToCacheForUsed(userId, coupons);
                break;
            case EXPIRED:
                result = addCouponToCacheForExpired(userId, coupons);
                break;
        }

        return result;
    }

    /**
     * 根据 status 获取到对应的 redis key
     */
    private String status2RedisKey(Integer status, Long userId) {
        String redisKey = null;
        CouponStatus couponStatus = CouponStatus.of(status);

        switch (couponStatus) {
            case USABLE:
                redisKey = String.format("%s%s", Constant.RedisPrefix.USER_COUPON_USABLE, userId);
                break;
            case USED:
                redisKey = String.format("%s%s", Constant.RedisPrefix.USER_COUPON_USED, userId);
                break;
            case EXPIRED:
                redisKey = String.format("%s%s", Constant.RedisPrefix.USER_COUPON_EXPIRED, userId);
                break;
            // CouponStatus.of()会抛出异常，所以couponStatus只能是三个中的一个，所以不需要default
        }

        return redisKey;
    }

    /**
     * 获取一个最小的过期时间
     * 缓存雪崩：key 在同一时间失效
     *
     * @param min 最小的小时数
     * @param max 最大的小时数
     * @return 返回 [min, max] 之间的随机秒数
     */
    @SuppressWarnings("all")
    private Long getRandomExpirationTime(Integer min, Integer max) {
        return RandomUtils.nextLong(min * 60L * 60L, max * 60L * 60L);
    }

    /**
     * 新增加优惠券到 Redis 中
     */
    @SuppressWarnings("all")
    private Integer addCouponToCacheForUsable(Long userId, List<Coupon> coupons) {
        // status 是 USABLE，代表是新增加的优惠券
        // 只会影响一个 Cache: USER_COUPON_USABLE
        log.debug("Add Coupon To Cache For Usable.");

        Map<String, String> needCachedObject = new HashMap<>(coupons.size() * 2);
        coupons.forEach(c -> needCachedObject.put(
                c.getId().toString(),
                JSON.toJSONString(c)
        ));

        String redisKey = status2RedisKey(CouponStatus.USABLE.getCode(), userId);
        redisTemplate.opsForHash().putAll(redisKey, needCachedObject);
        log.info("Add {} Coupon To Cache: {}, {}", needCachedObject.size(), userId, redisKey);

        redisTemplate.expire(redisKey, getRandomExpirationTime(1, 2), TimeUnit.SECONDS);

        return needCachedObject.size();
    }

    /**
     * 将已使用的优惠券加入到 Cache 中
     */
    @SuppressWarnings("all")
    private Integer addCouponToCacheForUsed(Long userId, List<Coupon> coupons) throws CouponException {
        // 如果 status 是 USED，代表用户操作是使用当前的优惠券，影响到两个Cache
        // USABLE，USED
        log.debug("Add Coupon To Cache For Used.");

        Map<String, String> needCachedForUsed = new HashMap<>(coupons.size() * 2);

        String redisKeyForUsable = status2RedisKey(CouponStatus.USABLE.getCode(), userId);

        String redisKeyForUsed = status2RedisKey(CouponStatus.USED.getCode(), userId);

        // 获取当前用户可用的优惠券
        List<Coupon> curUsableCoupons = getCachedCoupons(userId, CouponStatus.USABLE.getCode());
        // 当前可用的优惠券个数一定大于1（因为至少有一个无效的优惠券信息）
        assert curUsableCoupons.size() > coupons.size();

        coupons.forEach(c -> needCachedForUsed.put(
                c.getId().toString(),
                JSON.toJSONString(c)
        ));

        // 校验当前的优惠券参数是否与 Cache 中的匹配
        List<Integer> curUsableIds = curUsableCoupons.stream().map(Coupon::getId).collect(Collectors.toList());
        List<Integer> paramIds = coupons.stream().map(Coupon::getId).collect(Collectors.toList());

        if (!CollectionUtils.isSubCollection(paramIds, curUsableIds)) {
            log.error("CurCoupons Is Not Equal To Cache: {}, {}, {}",
                    userId, JSON.toJSONString(curUsableIds), JSON.toJSONString(paramIds));
            throw new CouponException("CurCoupons Is Not Equal To Cache");
        }

        List<String> needCleanKey = paramIds.stream()
                .map(Object::toString).collect(Collectors.toList());

        SessionCallback<Object> sessionCallback = new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                // 1. 已使用的优惠券 Cache 缓存添加
                operations.opsForHash().putAll(redisKeyForUsed, needCachedForUsed);

                // 2. 可用的优惠券 Cache 需要清理
                operations.opsForHash().delete(redisKeyForUsable, needCleanKey.toArray());

                // 3. 重置过期时间
                operations.expire(redisKeyForUsable, getRandomExpirationTime(1, 2), TimeUnit.SECONDS);
                operations.expire(redisKeyForUsed, getRandomExpirationTime(1, 2), TimeUnit.SECONDS);

                return null;
            }
        };

        log.info("Pipeline Exe Result: {}", JSON.toJSONString(redisTemplate.executePipelined(sessionCallback)));

        return coupons.size();
    }

    /**
     * 将过期优惠券加入到 Cache 中
     */
    @SuppressWarnings("all")
    private Integer addCouponToCacheForExpired(Long userId, List<Coupon> coupons) throws CouponException {

        // status 是 EXPIRED，代表是已有的优惠券过期，影响到两个 Cache
        // USABLE，EXPIRED
        log.debug("Add Coupon To Cache For Expired.");

        // 最终需要保存的 Cache
        Map<String, String> needCacheForExpired = new HashMap<>(coupons.size() * 2);

        String redisKeyForExpired = status2RedisKey(CouponStatus.EXPIRED.getCode(), userId);
        String redisKeyForUsable = status2RedisKey(CouponStatus.USABLE.getCode(), userId);

        // 用户当前可用的优惠券
        List<Coupon> curUsableCoupons = getCachedCoupons(userId, CouponStatus.USABLE.getCode());
        assert curUsableCoupons.size() > coupons.size();

        coupons.forEach(c -> needCacheForExpired.put(
                c.getId().toString(),
                JSON.toJSONString(c)
        ));

        // 校验当前的优惠券信息是否和 Cache 中的匹配。
        List<Integer> curUsableIds = curUsableCoupons.stream().map(Coupon::getId).collect(Collectors.toList());
        List<Integer> paramIds = coupons.stream().map(Coupon::getId).collect(Collectors.toList());

        if (!CollectionUtils.isSubCollection(paramIds, curUsableIds)) {
            log.error("CurCoupons Is Not Equal To Cache: {}, {}, {}",
                    userId, JSON.toJSONString(curUsableIds), JSON.toJSONString(paramIds));
            throw new CouponException("CurCoupons Is Not Equal To Cache");
        }

        List<String> needCleanKey = paramIds.stream().map(Objects::toString).collect(Collectors.toList());

        SessionCallback<Object> sessionCallback = new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                // 1. 已过期的优惠券添加到 Cache
                operations.opsForHash().putAll(redisKeyForExpired, needCacheForExpired);

                // 2. 清理后可用的优惠券记录添加到 Cache
                operations.opsForHash().delete(redisKeyForUsable, needCleanKey.toArray());

                // 3. 重置过期时间
                operations.expire(redisKeyForExpired, getRandomExpirationTime(1, 2), TimeUnit.SECONDS);
                operations.expire(redisKeyForUsable, getRandomExpirationTime(1, 2), TimeUnit.SECONDS);

                return null;
            }
        };

        log.info("Pipeline Exe Result: {}", JSON.toJSONString(redisTemplate.executePipelined(sessionCallback)));

        return coupons.size();
    }
}
