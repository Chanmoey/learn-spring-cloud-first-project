package com.moon.coupon.vo;

import com.moon.coupon.constant.CouponCategory;
import com.moon.coupon.constant.DistributeTarget;
import com.moon.coupon.constant.ProductLine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * 优惠券模板创建请求对象
 *
 * @author Chanmoey
 * @date 2022年07月24日
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplateRequest {

    private String name;

    private String logo;

    private String desc;

    /**
     * 传递 code
     */
    private String category;

    /**
     * 传递 code
     */
    private Integer productLine;

    private Integer count;

    private Long userId;

    /**
     * 传递 code
     */
    private Integer target;

    /**
     * 优惠券规则
     */
    private TemplateRule rule;

    public boolean validate() {
        boolean stringValid = StringUtils.isNotEmpty(name)
                && StringUtils.isNotEmpty(logo)
                && StringUtils.isNotEmpty(desc);

        boolean enumValid = null != CouponCategory.of(category)
                && null != ProductLine.of(productLine)
                && null != DistributeTarget.of(target);

        boolean numValid = count > 0 && userId > 0;

        return stringValid && enumValid && numValid && rule.validate();
    }
}
