package com.lp.config;

import jakarta.annotation.Resource;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;



/**
 * 跨域配置
 *
 * @author 10263
 */
@Configuration
public class GatewayConfig {

    @Resource
    private WebSocketGatewayFilter webSocketGatewayFilter;

    @Bean
    public CorsWebFilter corsWebFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        //配置跨域
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addAllowedOriginPattern("*");
        corsConfiguration.setAllowCredentials(true);
        source.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsWebFilter(source);
    }

    @Bean
    public RouteLocator redirectRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r.path("/websocket/ws/**")
                        .filters(f -> f.stripPrefix(1).filter(webSocketGatewayFilter))
                        .uri("lb://ws"))
                .route(r -> r.path("/api/web/**")
                        .filters(f -> f.rewritePath("/api/web/?(?<segment>.*)", "/$\\{segment}"))
                        .uri("lb://web"))
                .build();

    }
}
