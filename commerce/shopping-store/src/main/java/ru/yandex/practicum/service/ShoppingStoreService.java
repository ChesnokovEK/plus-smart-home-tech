package ru.yandex.practicum.service;

import ru.yandex.practicum.shoppingStore.dto.PageableDto;
import ru.yandex.practicum.shoppingStore.dto.ProductDto;
import ru.yandex.practicum.shoppingStore.dto.SetProductQuantityStateRequest;
import ru.yandex.practicum.shoppingStore.enums.ProductCategory;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface ShoppingStoreService {

    ProductDto createNewProduct(ProductDto productDto);

    boolean removeProductFromStore(UUID productId);

    List<ProductDto> getProductsByCategory(ProductCategory category, PageableDto pageableDto);

    ProductDto updateProduct(ProductDto productDto);

    boolean setProductQuantityState(SetProductQuantityStateRequest request);

    ProductDto getProduct(UUID productId);

    List<ProductDto> getProductsByIds(Collection<UUID> ids);
}
