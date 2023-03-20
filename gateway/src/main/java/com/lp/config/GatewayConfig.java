package com.lp.config;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * 跨域配置
 *
 * @author 10263
 */
@Configuration
public class GatewayConfig {


    @Bean
    public CorsWebFilter corsWebFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        //配置跨域
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
//        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedOriginPattern("*");
        corsConfiguration.setAllowCredentials(true);
        source.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsWebFilter(source);
    }

    @Bean
    public RouteLocator redirectRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("web", r -> r.path("/api/web/**")
                        .filters(f -> f.rewritePath("/api/web/?(?<segment>.*)", "/$\\{segment}"))
                        .uri("lb://web"))
                .route("ws", r -> {
                    try {
                        return r.path("/websocket/**")
                                .filters(f -> f.rewritePath("/websocket/?(?<segment>.*)", "/$\\{segment}"))
                                .uri("lb:ws://" + getName());
                    } catch (NacosException e) {
                        throw new RuntimeException(e);
                    }
                })
                .build();
    }

    private static String getName() throws NacosException {
        NamingService naming = NamingFactory.createNamingService("192.168.137.174");
        List<String> servicesOfServer = naming.getServicesOfServer(1, 10).getData();
        List<String> list = servicesOfServer.stream().filter(e -> e.startsWith("WS-")).collect(Collectors.toList());
        Random random = new Random();
        int n = random.nextInt(list.size());
        return list.get(n);
    }
}
