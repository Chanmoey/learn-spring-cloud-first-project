package com.moon.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * 1. @EnableZuulProxy 标识当前服务是Zuul服务。
 * 2. @SpringCloudApplication 开启了SpringBootApplication、服务发现、服务熔断。
 *
 * @author Chanmoey
 * @date 2022年07月23日
 */
@EnableZuulProxy
@SpringCloudApplication
public class ZuulGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ZuulGatewayApplication.class, args);
    }
}
