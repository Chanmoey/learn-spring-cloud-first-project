package com.moon.coupon.service.impl;

import com.alibaba.fastjson.JSON;
import com.moon.coupon.constant.Constant;
import com.moon.coupon.constant.CouponStatus;
import com.moon.coupon.service.IKafkaService;
import com.moon.coupon.vo.CouponKafkaMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

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
    /**
     * 消费优惠券 kafka 消息
     * kafka 有消息后，框架会自动调用此方法来消费消息
     *
     * @param record {@link ConsumerRecord}
     */
    @Override
    @KafkaListener(topics = {Constant.TOPIC}, groupId = "moon-coupon-1")
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
                    break;
                case EXPIRED:
                    break;
            }
        }

    }
}
