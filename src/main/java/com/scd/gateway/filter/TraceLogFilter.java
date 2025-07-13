package com.scd.gateway.filter;


import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@Slf4j
public class TraceLogFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        long startTime = System.currentTimeMillis();
        String traceId = UUID.randomUUID().toString();

        // 将traceId放入MDC上下文
        MDC.put("traceId", traceId);

        // 记录请求开始日志
        log.info("Request started | Method: {} | Path: {}", exchange.getRequest().getMethod(),
                exchange.getRequest().getPath());

        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .header("traceId", traceId)
                .build();

        return chain.filter(exchange.mutate().request(mutatedRequest).build())
                .doFinally(signalType -> {
            // 记录请求结束日志
            long duration = System.currentTimeMillis() - startTime;
            log.info("Request completed | Status: {} | Duration: {}ms",
                    exchange.getResponse().getStatusCode(),
                    duration);

            // 清除MDC上下文
            MDC.clear();
        });
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}

