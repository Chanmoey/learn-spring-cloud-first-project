package com.moon.coupon.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * @author Chanmoey
 * @date 2022年07月26日
 */
public interface IKafkaService {

    /**
     * 消费优惠券 kafka 消息
     * @param record {@link ConsumerRecord}
     */
    void consumeCouponKafkaMessage(ConsumerRecord<?, ?> record);
}
