package com.lp.config;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;

/**
 * 由于websocket服务是动态服务名，所以需要手动设置路由
 *
 * @author 10263
 */
@Slf4j
@Component
public class WebSocketGatewayFilter implements GatewayFilter {

    @Value("${spring.cloud.nacos.server-addr}")
    private String serverAddr;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Route route = exchange.getAttribute(GATEWAY_ROUTE_ATTR);
        String service = getName();
        Route newRoute = Route.async().asyncPredicate(route.getPredicate()).filters(route.getFilters()).id(route.getId())
                .order(route.getOrder()).uri(service).build();
        exchange.getAttributes().put(GATEWAY_ROUTE_ATTR, newRoute);
        return chain.filter(exchange);
    }


    private String getName() {
        NamingService naming;
        List<String> servicesOfServer;
        try {
            naming = NamingFactory.createNamingService(serverAddr);
            servicesOfServer = naming.getServicesOfServer(1, Integer.MAX_VALUE).getData();
        } catch (NacosException e) {
            throw new RuntimeException(e);
        }
        List<String> list = servicesOfServer.stream().filter(e -> e.startsWith("WS-")).collect(Collectors.toList());
        Random random = new Random();
        int n = random.nextInt(list.size());
        return "lb://" + list.get(n);
    }

}
