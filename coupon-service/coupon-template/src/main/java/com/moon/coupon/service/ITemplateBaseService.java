package com.moon.coupon.service;

import com.moon.coupon.dao.CouponTemplateDao;
import com.moon.coupon.entity.CouponTemplate;
import com.moon.coupon.exception.CouponException;
import com.moon.coupon.vo.CouponTemplateSDK;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 优惠券模板基础服务定义（CURD）
 *
 * @author Chanmoey
 * @date 2022年07月25日
 */
public interface ITemplateBaseService {

    /**
     * 根据优惠券模板 id 获取优惠券模板信息
     * @param id 模板 id
     * @return {@link CouponTemplate} 优惠券模板
     * @throws CouponException 异常
     */
    CouponTemplate buildTemplateInfo(Integer id) throws CouponException;

    /**
     * 查询所有可用的优惠券模板
     * @return {@link CouponTemplate} 优惠券模板
     */
    List<CouponTemplateSDK> findAllUsableTemplate();

    /**
     * 根据 id 集合查找优惠券模板
     * @param ids 模板 ids
     * @return Map<key: 模板 id, value: CouponTemplateSDK></> 优惠券模板
     */
    Map<Integer, CouponTemplateSDK> findIds2TemplateSDK(Collection<Integer> ids);
}
