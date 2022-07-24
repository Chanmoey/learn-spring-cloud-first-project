package com.moon.coupon.vo;

import com.moon.coupon.constant.PeriodType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * 优惠券规则对象定义
 *
 * @author Chanmoey
 * @date 2022年07月24日
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemplateRule {

    /**
     * 过期规则
     */
    private Expiration expiration;

    /**
     * 折扣规则
     */
    private Discount discount;

    /**
     * 每个人最多领取几张的规则
     */
    private Integer limitation;

    /**
     * 使用范围规则
     */
    private Usage usage;

    /**
     * 权重（可以和哪些优惠券叠加使用，同一类的优惠券一定不能叠加）：list[]，保存优惠券的唯一编码。
     */
    private String weight;

    public boolean validate() {
        Boolean numValid = limitation > 0;
        Boolean stringValid = StringUtils.isNotEmpty(weight);
        Boolean subRuleValid = expiration.validate() && discount.validate() && usage.validate();

        return numValid && stringValid && subRuleValid;
    }

    /**
     * 有效期规则
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Expiration {
        /**
         * 过期类型，对应 PeriodType 的 code 属性
         */
        private Integer period;

        /**
         * 有效间隔：只对变动型有效期有效
         */
        private Integer gap;

        /**
         * 优惠券模板的失效日期，两类郭泽都有效
         */
        private Long deadline;

        boolean validate() {
            return null != PeriodType.of(period)
                    && gap > 0
                    && deadline > 0;
        }
    }

    /**
     * 折扣规则，需要配合优惠券的类型决定
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Discount {

        /**
         * 额度：满减（20）、折扣（85），立减（10）
         */
        private Integer quota;

        /**
         * 基准，需要满多少才可用
         */
        private Integer base;

        boolean validate() {
            return quota > 0 && base > 0;
        }
    }

    /**
     * 使用范围
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Usage {
        /**
         * 省份
         */
        private String province;

        /**
         * 城市
         */
        private String city;

        /**
         * 商品类型，list[文娱、生鲜、家具、全品]
         */
        private String goodsType;

        boolean validate() {
            return StringUtils.isNoneEmpty(province)
                    && StringUtils.isNoneEmpty(city)
                    && StringUtils.isNotEmpty(goodsType);
        }
    }
}
