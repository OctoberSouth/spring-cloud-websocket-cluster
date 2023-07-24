package com.lp.config;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;

/**
 * 由于websocket服务是动态服务名，所以需要手动设置路由
 *
 * @author 10263
 */
@Slf4j
@Component
public class WebSocketGatewayFilter implements GatewayFilter {

    @Resource
    private NamingService namingService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Route route = exchange.getAttribute(GATEWAY_ROUTE_ATTR);
        String service = getName();
        Route newRoute = Route.async().asyncPredicate(route.getPredicate()).filters(route.getFilters()).id(route.getId())
                .order(route.getOrder()).uri(service).build();
        exchange.getAttributes().put(GATEWAY_ROUTE_ATTR, newRoute);
        return chain.filter(exchange);
    }


    /**
     * 随机获取一个socket服务名
     *
     * @return
     */
    private String getName() {
        try {
            List<String> servicesOfServer = namingService.getServicesOfServer(1, Integer.MAX_VALUE).getData();
            List<String> list = servicesOfServer.stream().filter(e -> e.startsWith("WS-")).toList();
            Random random = new Random();
            int n = random.nextInt(list.size());
            return "lb://" + list.get(n);
        } catch (NacosException e) {
            throw new RuntimeException(e);
        }
    }

}
