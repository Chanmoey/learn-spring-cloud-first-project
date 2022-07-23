package com.moon.coupon.fliter;

import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;

/**
 * @author Chanmoey
 * @date 2022年07月23日
 */
public abstract class  AbstractPostZuulFilter extends AbstractZuulFilter{
    @Override
    public String filterType() {
        return FilterConstants.POST_TYPE;
    }
}
