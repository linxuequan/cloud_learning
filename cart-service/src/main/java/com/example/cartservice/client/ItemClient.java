package com.example.cartservice.client;


import com.example.cartservice.domain.dto.ItemDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.List;

/**
 * OpenFeign利用动态代理实现这个方法，并且向http://item-service/items发送一个GET请求，携带ids为请求参数，并自动将返回值处理为List<ItemDTO>。
 */
@FeignClient("item-service")
public interface ItemClient {

    @GetMapping("/items")
    List<ItemDTO> queryItemByIds(@RequestParam("ids") Collection<Long> ids);
}