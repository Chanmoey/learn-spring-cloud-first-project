package com.moon.coupon.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.moon.coupon.constant.CouponCategory;
import com.moon.coupon.constant.DistributeTarget;
import com.moon.coupon.constant.ProductLine;
import com.moon.coupon.converter.CouponCategoryConverter;
import com.moon.coupon.converter.DistributeTargetConverter;
import com.moon.coupon.converter.ProductLineConverter;
import com.moon.coupon.converter.RuleConverter;
import com.moon.coupon.serialization.CouponTemplateSerialize;
import com.moon.coupon.vo.TemplateRule;
import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * 优惠券模板实体类
 *
 * @author Chanmoey
 * @date 2022年07月24日
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize(using = CouponTemplateSerialize.class)
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "coupon_template")
public class CouponTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    /**
     * 是否可用状态
     */
    @Column(name = "available", nullable = false)
    private Boolean available;

    @Column(name = "expired", nullable = false)
    private Boolean expired;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "logo", nullable = false)
    private String logo;

    @Column(name = "intro", nullable = false)
    private String desc;

    @Column(name = "category", nullable = false)
    @Convert(converter = CouponCategoryConverter.class)
    private CouponCategory category;

    @Column(name = "product_line", nullable = false)
    @Convert(converter = ProductLineConverter.class)
    private ProductLine productLine;

    @Column(name = "coupon_count", nullable = false)
    private Integer count;

    @CreatedDate
    @Column(name = "create_time", nullable = false)
    private Date createTime;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "template_key", nullable = false)
    private String key;

    @Column(name = "target", nullable = false)
    @Convert(converter = DistributeTargetConverter.class)
    private DistributeTarget target;

    @Column(name = "rule", nullable = false)
    @Convert(converter = RuleConverter.class)
    private TemplateRule rule;

    public CouponTemplate(String name, String logo, String desc, String category,
                          Integer productLine, Integer count, Long userId, Integer target,
                          TemplateRule rule) {
        this.available = false;
        this.expired = false;
        this.name = name;
        this.logo = logo;
        this.desc = desc;
        this.category = CouponCategory.of(category);
        this.productLine = ProductLine.of(productLine);
        this.count = count;
        this.userId = userId;

        // 4(产品线和类型) + 8（日期） + id（扩充4位）
        this.key = productLine.toString() + category + new SimpleDateFormat("yyyyMMdd").format(new Date());

        this.target = DistributeTarget.of(target);
        this.rule = rule;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        CouponTemplate that = (CouponTemplate) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
