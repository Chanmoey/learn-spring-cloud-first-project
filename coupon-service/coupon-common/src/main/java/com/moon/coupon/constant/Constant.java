package com.moon.coupon.constant;

/**
 * 通用常量定义
 *
 * @author Chanmoey
 * @date 2022年07月25日
 */
public class Constant {

    private Constant() {
    }

    private static final char[] NUMBER_NO_ZERO = {'1', '2', '3', '4', '5', '6', '7', '8', '9'};

    public static char[] getNumberNoZero() {
        return NUMBER_NO_ZERO;
    }

    /**
     * kafka 消息的 Topic
     */
    public static final String TOPIC = "moon_user_coupon_op";

    /**
     * Redis Key
     */
    public static class RedisPrefix {
        private RedisPrefix() {
        }

        /**
         * 优惠券码 key 前缀
         */
        public static final String COUPON_TEMPLATE = "moon_coupon_template_code_";

        /**
         * 用户当前可用的优惠券 key 前缀
         */
        public static final String USER_COUPON_USABLE = "moon_user_coupon_usable_";

        /**
         * 用户已使用的优惠券 key 前缀
         */
        public static final String USER_COUPON_USED = "moon_user_coupon_used_";

        /**
         * 用户当前已过期的优惠券 key 前缀
         */
        public static final String USER_COUPON_EXPIRED = "moon_user_coupon_expired_";
    }
}
