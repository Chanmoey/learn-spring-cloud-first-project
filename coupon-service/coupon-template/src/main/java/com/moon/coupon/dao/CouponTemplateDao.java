package com.moon.coupon.dao;

import com.moon.coupon.entity.CouponTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Chanmoey
 * @date 2022年07月24日
 */
@Repository
public interface CouponTemplateDao extends JpaRepository<CouponTemplate, Integer> {

    /**
     * 根据模板名称查询模板
     */
    CouponTemplate findByName(String name);

    /**
     * 根据是否过期以及是否可用查询模板
     */
    List<CouponTemplate> findAllByAvailableAndExpired(Boolean available, Boolean expired);

    /**
     * 根据 expired 标记查找模板记录
     */
    List<CouponTemplate> findAllByExpired(Boolean expired);
}
