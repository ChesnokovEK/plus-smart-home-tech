package ru.yandex.practicum.delivery.dto;

import lombok.*;
import ru.yandex.practicum.delivery.enums.DeliveryState;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryDto {
    private UUID deliveryId;
    private AddressDto fromAddress;
    private AddressDto toAddress;
    private UUID orderId;
    private DeliveryState state;
}
