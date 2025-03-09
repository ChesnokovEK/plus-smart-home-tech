package ru.yandex.practicum.service;

import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.payment.dto.PaymentDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentService {
    PaymentDto payment(OrderDto orderDto);

    BigDecimal productCost(OrderDto orderDto);

    BigDecimal getTotalCost(OrderDto orderDto);

    void paymentSuccess(UUID paymentId);

    void paymentFailed(UUID paymentId);
}
