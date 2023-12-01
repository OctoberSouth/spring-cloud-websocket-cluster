package com.lp.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;

/**
 * @author lp
 */
public class DynamicRoutingConfig {
    @Bean
    public RequestInterceptor cloudContextInterceptor() {
        return template -> {
            String url = template.url();
            if (url.startsWith("//")) {
                url = "http:" + url;
                /**
                 * ---------------------------------解释，解释可能不是很清楚，自己断点看下路径是怎么变化的---------------------------------------------
                 * 有带问号的url时， 这里需要截取url参数，
                 * 不然调用了template.target方法，已经给了参数了， 如 get http://test?param=1
                 * 再调用template.uri("")又给了一次，会变成传一个参数，接收成两个的问题  变成 http://test?param=1&param=1
                 * 不调用template.uri("")，uri会变成 http://test//test?param=1,所以我们需要把uri置空， 变成http://?param=1，最终变成我们正常的url http://test?param=1
                 */
                if (url.contains("?")) {
                    url = url.substring(0, url.indexOf("?"));
                }
                // 将拼好的路径作为我们的url，（要截取？号后面的，因为下面方法已经给了）
                template.target(url);
                // 这里需要把uri置空，不置空会变成两个， http://test//test?param=1
                template.uri("");
            }
        };
    }
}


