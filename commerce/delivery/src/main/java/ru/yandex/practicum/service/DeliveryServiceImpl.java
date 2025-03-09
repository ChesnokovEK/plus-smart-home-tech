package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.config.DeliveryCostConfig;
import ru.yandex.practicum.delivery.dto.DeliveryDto;
import ru.yandex.practicum.delivery.enums.DeliveryState;
import ru.yandex.practicum.exception.NoDeliveryFoundException;
import ru.yandex.practicum.exception.NoOrderFoundException;
import ru.yandex.practicum.mapper.DeliveryMapper;
import ru.yandex.practicum.entity.Delivery;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.order.feign.OrderClient;
import ru.yandex.practicum.repository.DeliveryRepository;
import ru.yandex.practicum.warehouse.dto.ShippedToDeliveryRequest;
import ru.yandex.practicum.warehouse.feign.WarehouseClient;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryMapper deliveryMapper;
    private final OrderClient orderClient;
    private final WarehouseClient warehouseClient;
    private final DeliveryCostConfig costConfig;


    @Override
    @Transactional
    public DeliveryDto planDelivery(DeliveryDto deliveryDto) {
        log.info("planDelivery {}", deliveryDto);

        deliveryDto.setState(DeliveryState.CREATED);

        Delivery delivery = deliveryMapper.fromDeliveryDto(deliveryDto);

        deliveryRepository.save(delivery);

        return deliveryMapper.toDeliveryDto(delivery);
    }

    @Override
    @Transactional(readOnly = true)
    public void deliverySuccessful(UUID orderId) {
        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Не найден заказ: " + orderId));

        delivery.setState(DeliveryState.DELIVERED);
        deliveryRepository.save(delivery);

        orderClient.delivery(delivery.getOrderId());
    }

    @Override
    @Transactional(readOnly = true)
    public void deliveryPicked(UUID deliveryId) {
        log.info("Получение товара для доставки: {}", deliveryId);

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new NoDeliveryFoundException("Доставка не найдена: " + deliveryId));

        delivery.setState(DeliveryState.IN_DELIVERY);
        deliveryRepository.save(delivery);

        ShippedToDeliveryRequest request = ShippedToDeliveryRequest.builder()
                .orderId(delivery.getOrderId())
                .deliveryId(deliveryId)
                .build();

        warehouseClient.shippedToDelivery(request);
    }

    @Override
    @Transactional(readOnly = true)
    public void deliveryFailed(UUID orderId) {
        log.info("delivery failed for orderId: {}", orderId);

        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new NoOrderFoundException("order no found: " + orderId));
        delivery.setState(DeliveryState.FAILED);
        deliveryRepository.save(delivery);

        orderClient.deliveryFailed(orderId);
    }

    @Override
    public BigDecimal deliveryCost(OrderDto orderDto) {

        Delivery delivery = deliveryRepository.findById(orderDto.getDeliveryId())
                .orElseThrow(() -> new NoDeliveryFoundException("no delivery found: " + orderDto.getDeliveryId()));

        delivery.setDeliveryWeight(orderDto.getDeliveryWeight());
        delivery.setDeliveryVolume(orderDto.getDeliveryVolume());
        delivery.setFragile(orderDto.isFragile());

        deliveryRepository.save(delivery);

        String warehouseAddress = String.valueOf(warehouseClient.getWarehouseAddress());

        BigDecimal baseCost = costByAddress(warehouseAddress);

        BigDecimal fragileAddition = orderDto.isFragile()
                ? baseCost.multiply(costConfig.getFragileMultiplier())
                : BigDecimal.ZERO;

        BigDecimal weightAddition = BigDecimal.valueOf(orderDto.getDeliveryWeight())
                .multiply(costConfig.getWeightMultiplier());

        BigDecimal volumeAddition = BigDecimal.valueOf(orderDto.getDeliveryVolume())
                .multiply(costConfig.getVolumeMultiplier());

        BigDecimal stepCost = baseCost
                .add(fragileAddition)
                .add(weightAddition)
                .add(volumeAddition);

        String deliveryStreet = delivery.getToAddress().getStreet();
        BigDecimal addressAddition = warehouseAddress.equals(deliveryStreet)
                ? BigDecimal.ZERO
                : stepCost.multiply(costConfig.getAddressMultiplier());

        BigDecimal totalCost = stepCost.add(addressAddition);

        log.info("delivery cost for order {}: {}", orderDto.getOrderId(), totalCost);
        return totalCost;
    }

    private BigDecimal costByAddress(String warehouseAddress) {
        BigDecimal warehouseMultiplier = costConfig.getWarehouseMultipliers().entrySet().stream()
                .filter(entry -> warehouseAddress.contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return costConfig.getBaseRate()
                .multiply(warehouseMultiplier)
                .add(costConfig.getBaseRate());
    }
}