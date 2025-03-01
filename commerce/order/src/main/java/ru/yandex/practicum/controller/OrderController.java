package ru.yandex.practicum.controller;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.order.dto.CreateNewOrderRequest;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.order.dto.ProductReturnRequest;
import ru.yandex.practicum.service.OrderService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    List<OrderDto> getClientOrders(@RequestParam String username) {
        return orderService.getClientOrders(username);
    }

    @PutMapping
    OrderDto createNewOrder(@Valid @RequestBody CreateNewOrderRequest createNewOrderRequest) {
        return orderService.createNewOrder(createNewOrderRequest);
    }

    @PostMapping("/return")
    OrderDto productReturn(@RequestBody ProductReturnRequest productReturnRequest) {
        return orderService.productReturn(productReturnRequest);
    }

    @PostMapping("/payment")
    OrderDto payment(@RequestParam UUID orderId) {
        return  orderService.payment(orderId);
    }

    @PostMapping("/payment/failed")
    OrderDto paymentFailed(@RequestParam UUID orderId) {
        return  orderService.paymentFailed(orderId);
    }

    @PostMapping("/delivery")
    OrderDto delivery(@RequestParam UUID orderId) {
        return orderService.delivery(orderId);
    }

    @PostMapping("/delivery/failed")
    OrderDto deliveryFailed(@RequestParam UUID orderId) {
        return orderService.deliveryFailed(orderId);
    }

    @PostMapping("/completed")
    OrderDto completed(@RequestParam UUID orderId) {
        return orderService.completed(orderId);
    }

    @PostMapping("/calculate/total")
    OrderDto calculateTotalCost(@RequestParam UUID orderId) {
        return orderService.calculateTotalCost(orderId);
    }

    @PostMapping("/calculate/delivery")
    OrderDto calculateDeliveryCost(@RequestParam UUID orderId) {
        return orderService.calculateDeliveryCost(orderId);
    }

    @PostMapping("/assembly")
    OrderDto assembly(@RequestParam UUID orderId) {
        return orderService.assembly(orderId);
    }

    @PostMapping("/assembly/failed")
    OrderDto assemblyFailed(@RequestParam UUID orderId) {
        return orderService.assemblyFailed(orderId);
    }
}
