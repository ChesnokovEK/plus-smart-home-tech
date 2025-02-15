package ru.yandex.practicum.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.yandex.practicum.entity.ShoppingCart;
import ru.yandex.practicum.shoppingCart.dto.ShoppingCartDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ShoppingCartMapper {
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "active", ignore = true)
    ShoppingCart toShoppingCart(final ShoppingCartDto productDto);

    ShoppingCartDto toShoppingCartDto(final ShoppingCart product);
}
