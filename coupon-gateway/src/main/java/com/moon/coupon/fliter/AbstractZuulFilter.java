package com.moon.coupon.fliter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

/**
 * 通用的抽象过滤器类。
 *
 * @author Chanmoey
 * @date 2022年07月23日
 */
public abstract class AbstractZuulFilter extends ZuulFilter {

    /**
     * 用于在过滤器之间传递消息，数据保存在每个请求的ThreadLocal中，
     */
    RequestContext context;

    private static final String NEXT = "next";

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        return (boolean) ctx.getOrDefault(NEXT, true);
    }

    @Override
    public Object run() throws ZuulException {
        this.context = RequestContext.getCurrentContext();
        return cRun();
    }

    protected abstract Object cRun();

    Object fail(int code, String msg) {
        // 已经失败，不需要继续执行过滤器。
        context.set(NEXT, false);
        context.setSendZuulResponse(false);
        context.getResponse().setContentType("text/html;charset=UTF-8");
        context.setResponseStatusCode(code);
        context.setResponseBody(String.format(
                "{\"result\": \"%s!\"}", msg
        ));

        return null;
    }

    Object success() {
        context.set(NEXT, true);
        return null;
    }
}
