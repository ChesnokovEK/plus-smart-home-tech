package ru.yandex.practicum.warehouse.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssemblyProductForOrderFromShoppingCartRequest {
    @NotNull(message = "shoppingCartId must be not empty")
    private UUID shoppingCartId;

    @NotNull(message = "orderId must be not empty")
    private UUID orderId;
}
