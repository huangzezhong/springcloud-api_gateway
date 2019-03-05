package com.gz.filter;

import com.google.common.util.concurrent.RateLimiter;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * 下单接口限流
 */
@Component
public class SaveOrderLimiterFilter extends ZuulFilter {

    /**
     * 令牌桶策略
     */
    private static final RateLimiter rateLimter = RateLimiter.create(10);

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return -4;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {

        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();

        if ("/apigateway/order/api/vi/order/save".equals(request.getRequestURI())) {
            if (!rateLimter.tryAcquire()) {
                requestContext.setSendZuulResponse(false);
                requestContext.setResponseStatusCode(HttpStatus.TOO_MANY_REQUESTS.value());
            }
        }
        return null;
    }
}
