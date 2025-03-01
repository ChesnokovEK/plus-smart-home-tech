package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.entity.Product;
import ru.yandex.practicum.exception.ProductNotFoundException;
import ru.yandex.practicum.mapper.ProductMapper;
import ru.yandex.practicum.repository.ProductRepository;
import ru.yandex.practicum.shoppingStore.dto.PageableDto;
import ru.yandex.practicum.shoppingStore.dto.ProductDto;
import ru.yandex.practicum.shoppingStore.enums.ProductCategory;
import ru.yandex.practicum.shoppingStore.dto.SetProductQuantityStateRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShoppingStoreServiceImpl implements ShoppingStoreService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public ProductDto createNewProduct(ProductDto productDto) {
        log.info("createNewProduct: {}", productDto);
        Product product = productMapper.toProduct(productDto);
        Product savedProduct = productRepository.save(product);
        return productMapper.toProductDto(savedProduct);
    }

    @Override
    @Transactional
    public boolean removeProductFromStore(UUID productId) {
        log.info("removeProductFromStore: {}", productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.error("not found product id {}", productId);
                    return new ProductNotFoundException("not found product");
                });

        productRepository.delete(product);
        return true;
    }

    @Override
    public List<ProductDto> getProductsByCategory(ProductCategory category, PageableDto pageableDto) {
        log.info("getProductsByCategory {}", category);
        Pageable pageable = convertToPageable(pageableDto);
        List<Product> products = productRepository.findAllByProductCategory(category, pageable).getContent();
        return products.stream()
                .map(productMapper::toProductDto)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public ProductDto updateProduct(ProductDto productDto) {
        UUID productId = productDto.getProductId();
        log.info("updateProduct: {}", productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("product not found"));

        updateProductFields(product, productDto);

        Product updatedProduct = productRepository.save(product);
        return productMapper.toProductDto(updatedProduct);
    }


    @Override
    @Transactional
    public boolean setProductQuantityState(SetProductQuantityStateRequest request) {
        UUID productId = request.getProductId();
        log.info("setProductQuantityState: {} - > {}", productId, request.getQuantityState());
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.error("not found product {}", productId);
                    return new ProductNotFoundException("not found product");
                });

        product.setQuantityState(request.getQuantityState());
        productRepository.save(product);
        return true;
    }

    @Override
    public ProductDto getProduct(UUID productId) {
        log.info("getProduct {}", productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.error("not found product {}", productId);
                    return new ProductNotFoundException("not found product");
                });

        return productMapper.toProductDto(product);
    }

    private void updateProductFields(Product product, ProductDto productDto) {
        if (productDto.getProductName() != null) {
            product.setProductName(productDto.getProductName());
        }
        if (productDto.getDescription() != null) {
            product.setDescription(productDto.getDescription());
        }
        if (productDto.getImageSrc() != null) {
            product.setImageSrc(productDto.getImageSrc());
        }
        if (productDto.getQuantityState() != null) {
            product.setQuantityState(productDto.getQuantityState());
        }
        if (productDto.getProductState() != null) {
            product.setProductState(productDto.getProductState());
        }
        if (productDto.getRating() > 0) {
            product.setRating(productDto.getRating());
        }
        if (productDto.getProductCategory() != null) {
            product.setProductCategory(productDto.getProductCategory());
        }
        if (productDto.getPrice() != null && productDto.getPrice().compareTo(BigDecimal.ONE) >= 0) {
            product.setPrice(productDto.getPrice());
        }
    }

    private Pageable convertToPageable(PageableDto pageableDto) {
        if (pageableDto.getSort() == null || pageableDto.getSort().isEmpty()) {
            return PageRequest.of(pageableDto.getPage(), pageableDto.getSize());
        }

        Sort sort = Sort.by(
                pageableDto.getSort().stream()
                        .map(sortStr -> {
                            String[] sortParams = sortStr.split(",");
                            if (sortParams.length == 2 && sortParams[1].equalsIgnoreCase("desc")) {
                                return Sort.Order.desc(sortParams[0]);
                            }
                            return Sort.Order.asc(sortParams[0]);
                        })
                        .toList()
        );

        return PageRequest.of(pageableDto.getPage(), pageableDto.getSize(), sort);
    }
}
