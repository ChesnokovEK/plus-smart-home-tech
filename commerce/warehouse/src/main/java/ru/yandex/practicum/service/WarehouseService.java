package ru.yandex.practicum.service;

import ru.yandex.practicum.shoppingCart.dto.ShoppingCartDto;
import ru.yandex.practicum.warehouse.dto.AddProductToWarehouseRequest;
import ru.yandex.practicum.warehouse.dto.AddressDto;
import ru.yandex.practicum.warehouse.dto.AssemblyProductForOrderFromShoppingCartRequest;
import ru.yandex.practicum.warehouse.dto.NewProductInWarehouseRequest;
import ru.yandex.practicum.warehouse.dto.BookedProductsDto;

import java.util.Map;
import java.util.UUID;

public interface WarehouseService {

    void addNewProduct(NewProductInWarehouseRequest request);

    void addProductQuantity(AddProductToWarehouseRequest request);

    BookedProductsDto bookProductForShoppingCart(ShoppingCartDto cartDto);

    void acceptReturn(Map<UUID, Integer> products);

    BookedProductsDto assemblyProductForOrderFromShoppingCart(AssemblyProductForOrderFromShoppingCartRequest request);

    AddressDto getWarehouseAddress();
}
