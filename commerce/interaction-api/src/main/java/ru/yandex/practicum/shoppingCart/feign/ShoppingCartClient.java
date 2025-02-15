package ru.yandex.practicum.shoppingCart.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.shoppingCart.dto.ShoppingCartDto;

import java.util.UUID;

@FeignClient(name = "shopping-cart-service", path = "/api/v1/shopping-cart")
public interface ShoppingCartClient {

    @GetMapping("/{id}")
    ShoppingCartDto getShoppingCartById(@PathVariable UUID id);
}