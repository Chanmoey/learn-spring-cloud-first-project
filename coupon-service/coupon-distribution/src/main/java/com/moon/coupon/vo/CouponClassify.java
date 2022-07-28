package com.moon.coupon.vo;

import com.moon.coupon.constant.CouponStatus;
import com.moon.coupon.constant.PeriodType;
import com.moon.coupon.entity.Coupon;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.time.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 根据优惠券状态，实现用户优惠券的分类
 *
 * @author Chanmoey
 * @date 2022年07月28日
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponClassify {

    private List<Coupon> usable;

    private List<Coupon> used;

    private List<Coupon> expired;

    /**
     * 对当前的优惠券进行分类
     */
    public static CouponClassify classify(List<Coupon> coupons) {
        List<Coupon> usable = new ArrayList<>(coupons.size());
        List<Coupon> used = new ArrayList<>(coupons.size());
        List<Coupon> expired = new ArrayList<>(coupons.size());

        coupons.forEach(c -> {

            // 判断优惠券是否过期
            boolean isTimeExpired;
            long curTime = new Date().getTime();

            // 固定日期过期类型
            if (c.getTemplateSDK().getRule().getExpiration().getPeriod()
                    .equals(PeriodType.REGULAR.getCode())) {
                isTimeExpired = c.getTemplateSDK().getRule().getExpiration().getDeadline() <= curTime;

                // 变动日期
            } else {
                isTimeExpired = DateUtils.addDays(
                        c.getAssignTime(),
                        c.getTemplateSDK().getRule().getExpiration().getGap()
                ).getTime() <= curTime;
            }

            if (c.getStatus() == CouponStatus.USED) {
                used.add(c);
            } else if (c.getStatus() == CouponStatus.EXPIRED || isTimeExpired) {
                expired.add(c);
            } else {
                usable.add(c);
            }
        });

        return new CouponClassify(usable, used, expired);
    }
}
