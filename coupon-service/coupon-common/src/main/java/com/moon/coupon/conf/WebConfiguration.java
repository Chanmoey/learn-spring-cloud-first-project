package com.moon.coupon.conf;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * <h1>定制 HTTP 消息转换器</h1>
 *
 * @author Chanmoey
 * @date 2022年07月24日
 */
@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    /**
     * HTTP JSON 内容向 Java 对象的转换，以及 Java 对象向 HTTP JSON 的转换。
     * @param converters 转换器列表
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {

        converters.clear();
        converters.add(new MappingJackson2HttpMessageConverter());
    }
}
