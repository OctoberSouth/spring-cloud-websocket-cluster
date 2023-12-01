package com.lp.config;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;


/**
 * 由于websocket服务是动态服务名，所以需要手动设置路由
 *
 * @author lp
 */
@Slf4j
@Component
public class WebSocketGatewayFilter implements GatewayFilter {
    @Resource
    private DiscoveryClient discoveryClient;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //匹配websocket服务器
        Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
        String service = getName();
        Route newRoute = null;
        if (route != null) {
            newRoute = Route.async().asyncPredicate(route.getPredicate()).filters(route.getFilters()).id(route.getId())
                    .order(route.getOrder()).uri(service).build();
        }
        exchange.getAttributes().put(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR, newRoute);
        return chain.filter(exchange);
    }

    /**
     * 随机获取一个socket服务名
     *
     * @return
     */
    private String getName() {
        List<String> servicesOfServer = discoveryClient.getServices();
        List<String> list = servicesOfServer.stream().filter(e -> e.startsWith("ws-server")).toList();
        Random random = new Random();
        int n = random.nextInt(list.size());
        return "lb://" + list.get(n);
    }
}
