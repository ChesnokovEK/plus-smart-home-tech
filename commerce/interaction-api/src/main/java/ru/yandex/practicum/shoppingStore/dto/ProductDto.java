package ru.yandex.practicum.shoppingStore.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.shoppingStore.enums.ProductCategory;
import ru.yandex.practicum.shoppingStore.enums.ProductState;
import ru.yandex.practicum.shoppingStore.enums.QuantityState;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDto {
    private UUID productId;

    @NotBlank
    private String productName;
    @NotBlank
    private String description;

    private String imageSrc;

    @NotNull
    private QuantityState quantityState;

    @NotNull
    private ProductState productState;

    @DecimalMin(value = "1.0", message = "rate must be greater 0")
    @DecimalMax(value = "5.0", message = "rate must be less 5")
    private double rating;

    @NotNull(message = "Категория товара не должна быть пустой")
    private ProductCategory productCategory;

    @DecimalMin(value = "1.0", message = "Цена товара должна быть не меньше 1")
    private BigDecimal price;
}