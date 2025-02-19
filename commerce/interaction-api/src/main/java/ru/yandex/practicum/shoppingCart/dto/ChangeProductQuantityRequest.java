package ru.yandex.practicum.shoppingCart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangeProductQuantityRequest {

    @NotNull(message = "Product id must be not empty")
    private UUID productId;

    @Min(value = 0, message = "newQuantity must greater then 0")
    private int newQuantity;
}
