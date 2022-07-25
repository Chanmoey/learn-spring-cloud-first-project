package com.moon.coupon.service.impl;

import com.moon.coupon.dao.CouponTemplateDao;
import com.moon.coupon.entity.CouponTemplate;
import com.moon.coupon.exception.CouponException;
import com.moon.coupon.service.ITemplateBaseService;
import com.moon.coupon.vo.CouponTemplateSDK;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 优惠券模板基础服务定义（CURD）
 *
 * @author Chanmoey
 * @date 2022年07月25日
 */
@Slf4j
@Service
public class TemplateBaseServiceImpl implements ITemplateBaseService {

    private final CouponTemplateDao templateDao;

    @Autowired
    public TemplateBaseServiceImpl(CouponTemplateDao templateDao) {
        this.templateDao = templateDao;
    }

    /**
     * 根据优惠券模板 id 获取优惠券模板信息
     *
     * @param id 模板 id
     * @return {@link CouponTemplate} 优惠券模板
     * @throws CouponException 异常
     */
    public CouponTemplate buildTemplateInfo(Integer id) throws CouponException {
        Optional<CouponTemplate> template = templateDao.findById(id);
        return template.orElseThrow(() -> new CouponException("Template Is Not Exist: " + id));
    }

    /**
     * 查询所有可用的优惠券模板
     *
     * @return {@link CouponTemplate} 优惠券模板
     */
    public List<CouponTemplateSDK> findAllUsableTemplate() {

        List<CouponTemplate> templates = templateDao.findAllByAvailableAndExpired(true, false);

        return templates.stream().map(this::template2TemplateSDK).collect(Collectors.toList());
    }

    /**
     * 根据 id 集合查找优惠券模板
     *
     * @param ids 模板 ids
     * @return Map<key: 模板 id, value: CouponTemplateSDK></> 优惠券模板
     */
    public Map<Integer, CouponTemplateSDK> findIds2TemplateSDK(Collection<Integer> ids) {
        List<CouponTemplate> templates = templateDao.findAllById(ids);

        return templates.stream().map(this::template2TemplateSDK).collect(Collectors.toMap(
                CouponTemplateSDK::getId, Function.identity()
        ));
    }

    private CouponTemplateSDK template2TemplateSDK(CouponTemplate template) {
        return new CouponTemplateSDK(
                template.getId(),
                template.getName(),
                template.getLogo(),
                template.getDesc(),
                template.getCategory().getCode(),
                template.getProductLine().getCode(),
                template.getKey(), // 并不是拼装好的 Template Key
                template.getTarget().getCode(),
                template.getRule()
        );
    }
}
