package com.moon.coupon.fliter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Chanmoey
 * @date 2022年07月23日
 */
@Slf4j
@Component
public class AccessLogFilter extends AbstractPostZuulFilter {

    @Override
    protected Object cRun() {
        HttpServletRequest request = context.getRequest();

        // 从 PreRequestFilter中取得请求的时间戳。
        Long startTime = (Long) context.get("startTime");
        String uri = request.getRequestURI();
        Long duration = System.currentTimeMillis() - startTime;

        // 打印接口的响应时间
        log.info("uri: {}, duration: {}", uri, duration);
        return success();
    }

    @Override
    public int filterOrder() {
        // 最终的过滤器。
        return FilterConstants.SEND_RESPONSE_FILTER_ORDER;
    }
}
