package com.moon.coupon.service.impl;

import com.moon.coupon.dao.CouponTemplateDao;
import com.moon.coupon.entity.CouponTemplate;
import com.moon.coupon.exception.CouponException;
import com.moon.coupon.service.IAsyncService;
import com.moon.coupon.service.IBuildTemplateService;
import com.moon.coupon.vo.TemplateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 构建优惠券模板实现
 *
 * @author Chanmoey
 * @date 2022年07月25日
 */
@Service
public class BuildTemplateServiceImpl implements IBuildTemplateService {

    /**
     * 异步服务
     */
    private final IAsyncService asyncService;

    private final CouponTemplateDao templateDao;

    @Autowired
    public BuildTemplateServiceImpl(IAsyncService asyncService, CouponTemplateDao templateDao) {
        this.asyncService = asyncService;
        this.templateDao = templateDao;
    }

    /**
     * 创建优惠券模板
     *
     * @param request {@link TemplateRequest} 模板请求对象
     * @return {@link CouponTemplate} 优惠券模板实体
     */
    @Override
    public CouponTemplate buildTemplate(TemplateRequest request) throws CouponException {

        if (!request.validate()) {
            throw new CouponException("Param Is Not Valid!");
        }

        if (null != templateDao.findByName(request.getName())) {
            throw new CouponException("Exist Same Name Template!");
        }

        CouponTemplate template = requestToTemplate(request);
        template = templateDao.save(template);

        // 根据优惠券模板异步生成优惠券码
        asyncService.asyncConstructCouponByTemplate(template);

        return template;
    }

    /**
     * 将 TemplateRequest 转为 CouponTemplate
     */
    private CouponTemplate requestToTemplate(TemplateRequest request) {
        return new CouponTemplate(
                request.getName(),
                request.getLogo(),
                request.getDesc(),
                request.getCategory(),
                request.getProductLine(),
                request.getCount(),
                request.getUserId(),
                request.getTarget(),
                request.getRule()
        );
    }
}
