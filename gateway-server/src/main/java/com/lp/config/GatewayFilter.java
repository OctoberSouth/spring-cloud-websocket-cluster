package com.lp.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * 跨域配置
 *
 * @author lp
 */
@Configuration
public class GatewayFilter {


    @Bean
    public RouteLocator redirectRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r.path("/websocket/**").filters(f -> f.stripPrefix(1)).uri("lb://websocket-server"))
                .build();

    }


}
