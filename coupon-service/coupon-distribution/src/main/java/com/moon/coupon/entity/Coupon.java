package com.moon.coupon.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.moon.coupon.constant.CouponStatus;
import com.moon.coupon.converter.CouponStatusConverter;
import com.moon.coupon.serialization.CouponSerialize;
import com.moon.coupon.vo.CouponTemplateSDK;
import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

/**
 * 优惠券（用户领取的优惠券记录）实体表
 *
 * @author Chanmoey
 * @date 2022年07月26日
 */
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "coupon")
@JsonSerialize(using = CouponSerialize.class)
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "template_id", nullable = false)
    private Integer templateId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "coupon_code", nullable = false)
    private String couponCode;

    @CreatedDate
    @Column(name = "assign_time", nullable = false)
    private Date assignTime;

    @Convert(converter = CouponStatusConverter.class)
    @Column(name = "status", nullable = false)
    private CouponStatus status;

    /**
     * 优惠券模板信息，但是不属于 Coupon 表的字段
     */
    @Transient
    private CouponTemplateSDK templateSDK;

    /**
     * 返回一个无效的 Coupon 对象
     */
    public static Coupon invalidCoupon() {
        Coupon coupon = new Coupon();
        coupon.setId(-1);
        return coupon;
    }

    /**
     * 构造函数
     */
    public Coupon(Integer templateId, Long userId, String couponCode, CouponStatus status) {
        this.templateId = templateId;
        this.userId = userId;
        this.couponCode = couponCode;
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Coupon coupon = (Coupon) o;
        return id != null && Objects.equals(id, coupon.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
