package com.moon.coupon.service.impl;

import com.alibaba.fastjson.JSON;
import com.moon.coupon.constant.Constant;
import com.moon.coupon.constant.CouponStatus;
import com.moon.coupon.dao.CouponDao;
import com.moon.coupon.entity.Coupon;
import com.moon.coupon.service.IKafkaService;
import com.moon.coupon.vo.CouponKafkaMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Kafka 相关的服务接口实现
 * 核心思想： 将 Cache 中的 Coupon 的状态变化同步到 DB 中
 *
 * @author Chanmoey
 * @date 2022年07月27日
 */
@Slf4j
@Service
public class KafkaServiceImpl implements IKafkaService {

    private final CouponDao couponDao;

    @Autowired
    public KafkaServiceImpl(CouponDao couponDao) {
        this.couponDao = couponDao;
    }

    /**
     * 消费优惠券 kafka 消息
     * kafka 有消息后，框架会自动调用此方法来消费消息
     *
     * @param record {@link ConsumerRecord}
     */
    @Override
    @KafkaListener(topics = {Constant.TOPIC}, groupId = "moon-coupon-1")
    @SuppressWarnings("all")
    public void consumeCouponKafkaMessage(ConsumerRecord<?, ?> record) {

        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {
            Objects message = (Objects) kafkaMessage.get();
            CouponKafkaMessage couponInfo = JSON.parseObject(message.toString(), CouponKafkaMessage.class);

            log.info("Receive CouponKafkaMessage: {}", message);

            CouponStatus status = CouponStatus.of(couponInfo.getStatus());

            switch (status) {
                case USABLE:

                    break;
                case USED:
                    processUsedCoupons(couponInfo, status);
                    break;
                case EXPIRED:
                    processExpiredCoupons(couponInfo, status);
                    break;
            }
        }

    }

    /**
     * 处理已使用的用户优惠券
     */
    private void processUsedCoupons(CouponKafkaMessage kafkaMessage, CouponStatus status) {
        // T D 给用户发送短信
        processCouponsByStatus(kafkaMessage, status);
    }

    /**
     * 处理已过期的用户优惠券
     */
    private void processExpiredCoupons(CouponKafkaMessage kafkaMessage, CouponStatus status) {
        // T D 给用户发送推送
        processUsedCoupons(kafkaMessage, status);
    }

    /**
     * 根据优惠券状态消费优惠券消息
     */
    private void processCouponsByStatus(CouponKafkaMessage kafkaMessage, CouponStatus status) {
        List<Coupon> coupons = couponDao.findAllById(kafkaMessage.getIds());
        if (CollectionUtils.isEmpty(coupons) || coupons.size() != kafkaMessage.getIds().size()) {
            log.error("Can Nof Find Right Coupon Info: {}", JSON.toJSONString(kafkaMessage));
            // 发送邮件
            return;
        }

        coupons.forEach(c -> c.setStatus(status));
        log.info("CouponKafkaMessage Op Coupon Count: {}", couponDao.saveAll(coupons).size());
    }
}
