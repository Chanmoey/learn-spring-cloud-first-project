package com.moon.coupon.fliter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * 校验请求中传递的Token
 *
 * @author Chanmoey
 * @date 2022年07月23日
 */
@Slf4j
@Component
public class TokenFilter extends AbstractPreZuulFilter {
    @Override
    protected Object cRun() {
        // 在执行cRun之前，run一定被执行了，所以context已经获取到了。
        HttpServletRequest request = context.getRequest();
        log.info("{} request to {}", request.getMethod(), request.getRequestURI());

        Object token = request.getParameter("token");
        if (null == token) {
            log.error("error: token is empty!");
            return fail(401, "error: token is empty!");
        }
        return success();
    }

    @Override
    public int filterOrder() {
        return 1;
    }
}
