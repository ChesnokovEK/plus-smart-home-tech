package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.delivery.dto.DeliveryDto;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.service.DeliveryService;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/delivery")
@RequiredArgsConstructor
public class DeliveryController {
    private final DeliveryService deliveryService;

    @PutMapping
    DeliveryDto planDelivery(@RequestBody DeliveryDto deliveryDto) {
        return deliveryService.planDelivery(deliveryDto);
    }

    @PostMapping("/picked")
    void deliveryPicked(@RequestParam UUID deliveryId) {
        deliveryService.deliveryPicked(deliveryId);
    }

    @PostMapping("/failed")
    void deliveryFailed(@RequestParam UUID orderId) {
        deliveryService.deliveryFailed(orderId);
    }

    @PostMapping("/successful")
    void deliverySuccessful(@RequestParam UUID orderId) {
        deliveryService.deliverySuccessful(orderId);
    }

    @PostMapping
    BigDecimal deliveryCost(@RequestBody OrderDto orderDto) {
        return deliveryService.deliveryCost(orderDto);
    }
}