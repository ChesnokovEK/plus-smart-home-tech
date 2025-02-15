package ru.yandex.practicum.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.entity.ShoppingCart;
import ru.yandex.practicum.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.exception.NotAuthorizedUserException;
import ru.yandex.practicum.exception.ProductNotAvailableException;
import ru.yandex.practicum.mapper.ShoppingCartMapper;
import ru.yandex.practicum.repository.ShoppingCartRepository;
import ru.yandex.practicum.shoppingCart.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.shoppingCart.dto.ShoppingCartDto;
import ru.yandex.practicum.shoppingStore.dto.ProductDto;
import ru.yandex.practicum.shoppingStore.enums.ProductState;
import ru.yandex.practicum.shoppingStore.enums.QuantityState;
import ru.yandex.practicum.shoppingStore.feign.ShoppingStoreClient;
import ru.yandex.practicum.warehouse.dto.BookedProductsDto;
import ru.yandex.practicum.warehouse.feign.WarehouseClient;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private static final int LIMITED_COUNT = 5;
    private static final int ENOUGH_COUNT = 20;
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingStoreClient shoppingStoreClient;
    private final WarehouseClient warehouseClient;
    private final ShoppingCartMapper shoppingCartMapper;

    @Override
    public ShoppingCartDto getShoppingCart(String username) {
        log.info("getShoppingCart user: {}", username);
        validateUsername(username);

        ShoppingCart shoppingCart = shoppingCartRepository.findByUsernameAndActive(username, true)
                .orElseGet(() -> createNewShoppingCart(username));
        return shoppingCartMapper.toShoppingCartDto(shoppingCart);
    }

    @Override
    @Transactional
    public ShoppingCartDto addProducts(String username, Map<UUID, Integer> products) {
        log.info("addProducts user: {}", username);
        validateUsername(username);

        ShoppingCart shoppingCart = getActiveShoppingCart(username);

        for (Map.Entry<UUID, Integer> entry : products.entrySet()) {
            UUID productId = entry.getKey();
            int quantity = entry.getValue();
            checkProductQuantityState(productId, quantity);
            updateProductQuantity(shoppingCart, productId, quantity);
        }
        shoppingCartRepository.save(shoppingCart);
        return shoppingCartMapper.toShoppingCartDto(shoppingCart);
    }

    @Override
    public void deactivateShoppingCart(String username) {
        log.info("deactivateShoppingCart user: {}", username);
        validateUsername(username);
        ShoppingCart shoppingCart = getActiveShoppingCart(username);

        shoppingCart.setActive(false);
        shoppingCartRepository.save(shoppingCart);
        log.info("Корзина пользователя {} успешно деактивирована.", username);
    }

    @Override
    @Transactional
    public ShoppingCartDto removeProducts(String username, Map<UUID, Integer> products) {
        log.info("Удаление товаров из корзины для пользователя: {}", username);
        validateUsername(username);

        ShoppingCart shoppingCart = getActiveShoppingCart(username);

        for (Map.Entry<UUID, Integer> entry : products.entrySet()) {
            UUID productId = entry.getKey();
            int quantityToRemove = entry.getValue();
            updateProductQuantity(shoppingCart, productId, -quantityToRemove);
        }

        shoppingCartRepository.save(shoppingCart);
        ShoppingCartDto shoppingCartDto = shoppingCartMapper.toShoppingCartDto(shoppingCart);
        log.info("Корзина пользователя {} успешно обновлена: {}", username, shoppingCartDto);
        return shoppingCartDto;
    }

    @Override
    @Transactional
    public ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request) {
        log.info("changeProductQuantity user: {}", username);
        validateUsername(username);

        ShoppingCart shoppingCart = getActiveShoppingCart(username);

        UUID productId = request.getProductId();
        int newQuantity = request.getNewQuantity();

        if (!shoppingCart.getProducts().containsKey(productId)) {
            throw new NoProductsInShoppingCartException("no in cart product: " + productId);
        }

        checkProductQuantityState(productId, newQuantity);

        shoppingCart.getProducts().put(productId, newQuantity);
        shoppingCartRepository.save(shoppingCart);

        return shoppingCartMapper.toShoppingCartDto(shoppingCart);
    }

    @Override
    @Transactional
    public BookedProductsDto bookProducts(String username) {
        log.info("bookProducts user: {}", username);
        validateUsername(username);

        ShoppingCart shoppingCart = getActiveShoppingCart(username);

        Map<UUID, Integer> products = shoppingCart.getProducts();
        if (products.isEmpty()) {
            throw new NoProductsInShoppingCartException("product cart is empty, user " + username);
        }

        try {
            BookedProductsDto bookedProducts = warehouseClient.bookProducts(shoppingCartMapper.toShoppingCartDto(shoppingCart));

            shoppingCart.setActive(false);
            shoppingCartRepository.save(shoppingCart);

            log.info("booking completed, user {}", username);
            return bookedProducts;
        } catch (Exception e) {
            log.error("booking error, user {}, cause {}", username, e.getMessage(), e);
            throw new RuntimeException("booking error: " + e.getMessage(), e);
        }
    }

    private ShoppingCart getActiveShoppingCart(String username) {
        return shoppingCartRepository.findByUsernameAndActive(username, true)
                .orElseThrow(() -> new NoProductsInShoppingCartException("product cart not found for user: " + username));
    }

    private void updateProductQuantity(ShoppingCart shoppingCart, UUID productId, int quantityChange) {
        shoppingCart.getProducts().merge(productId, quantityChange, (current, change) -> {
            int updatedQuantity = current + change;
            return updatedQuantity > 0 ? updatedQuantity : null;
        });
    }

    private void validateUsername(String username) {
        if (username == null || username.isEmpty()) {
            throw new NotAuthorizedUserException("username is empty");
        }
    }

    private ShoppingCart createNewShoppingCart(String username) {
        log.info("createNewShoppingCart user: {}", username);
        ShoppingCart cart = new ShoppingCart();
        cart.setUsername(username);
        cart.setActive(true);
        cart.setProducts(new HashMap<>());
        return shoppingCartRepository.save(cart);
    }

    private void checkProductQuantityState(UUID productId, int quantity) {
        ProductDto productDto = shoppingStoreClient.getProduct(productId);
        if (productDto == null || productDto.getProductState() != ProductState.ACTIVE) {
            throw new ProductNotAvailableException("product not available: " + productId);
        }

        QuantityState quantityState = productDto.getQuantityState();
        switch (quantityState) {
            case ENDED -> throw new ProductNotAvailableException("no product available: " + productId);
            case FEW -> {
                if (quantity > LIMITED_COUNT) {
                    throw new ProductNotAvailableException(
                            "No product id " + productId + "available for request quantity " + quantity);
                }
            }
            case ENOUGH -> {
                if (quantity >= ENOUGH_COUNT) {
                    throw new ProductNotAvailableException(
                            "No product id " + productId + "available for request quantity " + quantity);
                }
            }
            default -> throw new IllegalStateException("illegal product state: " + quantityState);
        }
    }
}
