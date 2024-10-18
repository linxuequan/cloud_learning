package com.example.hmservicegateway.filter;

import com.example.hmservicegateway.config.AuthProperties;
import com.example.hmservicegateway.utils.JwtTool;
import com.hmall.common.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import springfox.documentation.service.Server;

import java.util.List;

@RequiredArgsConstructor
@Component
public class AutoGlobalFilter implements GlobalFilter, Ordered {
    private final AuthProperties authProperties;
    private final JwtTool jwtTool;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        if(isExclude(request.getPath().toString())){
            return chain.filter(exchange);
        }
        String token = null;
        List<String> headers = request.getHeaders().get("authorization");
        if(headers != null && !headers.isEmpty()){
            token = headers.get(0);
        }
        Long userId = null;
        try {
            userId = jwtTool.parseToken(token);
        }catch (UnauthorizedException e){
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        System.out.println(userId);
        String userInfo = userId.toString();
        ServerWebExchange ex = exchange.mutate().request(builder -> builder.header("user-Info",userInfo)).build();
        return chain.filter(ex);
    }

    private boolean isExclude(String path) {
        for(String pathPattern : authProperties.getExcludePaths()){
            if(antPathMatcher.match(pathPattern,path))
                return true;
        }
        return false;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
