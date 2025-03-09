package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.delivery.dto.AddressDto;
import ru.yandex.practicum.delivery.dto.DeliveryDto;
import ru.yandex.practicum.delivery.enums.DeliveryState;
import ru.yandex.practicum.delivery.feign.DeliveryClient;
import ru.yandex.practicum.exception.NoOrderFoundException;
import ru.yandex.practicum.exception.NotAuthorizedUserException;
import ru.yandex.practicum.mapper.AddressMapper;
import ru.yandex.practicum.mapper.OrderMapper;
import ru.yandex.practicum.entity.Order;
import ru.yandex.practicum.order.dto.CreateNewOrderRequest;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.order.dto.ProductReturnRequest;
import ru.yandex.practicum.order.enums.OrderState;
import ru.yandex.practicum.payment.dto.PaymentDto;
import ru.yandex.practicum.payment.feign.PaymentClient;
import ru.yandex.practicum.repository.OrderRepository;
import ru.yandex.practicum.warehouse.dto.AssemblyProductForOrderFromShoppingCartRequest;
import ru.yandex.practicum.warehouse.dto.BookedProductsDto;
import ru.yandex.practicum.warehouse.feign.WarehouseClient;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final WarehouseClient warehouseClient;
    private final DeliveryClient deliveryClient;
    private final PaymentClient paymentClient;
    private final OrderMapper orderMapper;
    private final AddressMapper addressMapper;


    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getClientOrders(String username) {
        if (username == null || username.isEmpty()) {
            throw new NotAuthorizedUserException("Имя пользователя не должно быть пустым");
        }

        List<Order> orders = orderRepository.findByUsername(username);

        return orders.stream()
                .map(orderMapper::toOrderDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderDto createNewOrder(CreateNewOrderRequest createNewOrderRequest) {
        log.info("Создание нового заказа");

        UUID shoppingCartId = createNewOrderRequest.getShoppingCart().getShoppingCartId();

        Order order = Order.builder()
                .shoppingCartId(shoppingCartId)
                .username(createNewOrderRequest.getUsername())
                .products(createNewOrderRequest.getShoppingCart().getProducts())
                .state(OrderState.NEW)
                .build();

        Order savedOrder = orderRepository.save(order);

        BookedProductsDto bookedProduct = warehouseClient.assemblyProductForOrderFromShoppingCart(
                new AssemblyProductForOrderFromShoppingCartRequest(savedOrder.getOrderId(), shoppingCartId)
        );

        savedOrder.setDeliveryWeight(bookedProduct.getDeliveryWeight());
        savedOrder.setDeliveryVolume(bookedProduct.getDeliveryVolume());
        savedOrder.setFragile(bookedProduct.isFragile());
        savedOrder.setFromAddress(null);
        savedOrder.setToAddress(addressMapper.fromAddressDto(createNewOrderRequest.getAddress()));

        orderRepository.save(order);

        AddressDto fromAddress = warehouseClient.getWarehouseAddress();
        order.setFromAddress(addressMapper.fromAddressDto(fromAddress));

        DeliveryDto deliveryDto = DeliveryDto.builder()
                .deliveryId(UUID.randomUUID())
                .orderId(order.getOrderId())
                .fromAddress(fromAddress)
                .toAddress(addressMapper.toAddressDto(order.getToAddress()))
                .state(DeliveryState.CREATED)
                .build();

        DeliveryDto createdDelivery = deliveryClient.planDelivery(deliveryDto);
        order.setDeliveryId(createdDelivery.getDeliveryId());

        BigDecimal productPrice = paymentClient.productCost(orderMapper.toOrderDto(order));
        order.setProductPrice(productPrice);

        BigDecimal deliveryPrice = deliveryClient.deliveryCost(orderMapper.toOrderDto(order));
        order.setDeliveryPrice(deliveryPrice);

        BigDecimal totalPrice = paymentClient.getTotalCost(orderMapper.toOrderDto(order));
        order.setTotalPrice(totalPrice);

        orderRepository.save(order);

        log.info("Заказ создан: {}", order.getOrderId());

        return orderMapper.toOrderDto(order);
    }

    @Override
    @Transactional
    public OrderDto payment(UUID orderId) {
        log.info("Инициация оплаты для заказа: {}", orderId);
        Order order = getOrderById(orderId);

        PaymentDto paymentDto = paymentClient.payment(orderMapper.toOrderDto(order));
        order.setPaymentId(paymentDto.getPaymentId());
        order.setState(OrderState.PAID);

        orderRepository.save(order);

        return orderMapper.toOrderDto(order);
    }

    @Override
    @Transactional
    public OrderDto paymentFailed(UUID orderId) {
        log.info("Обработка неудачной оплаты для заказа: {}", orderId);
        Order order = getOrderById(orderId);

        order.setState(OrderState.PAYMENT_FAILED);
        orderRepository.save(order);

        return orderMapper.toOrderDto(order);
    }

    @Override
    @Transactional
    public OrderDto delivery(UUID orderId) {
        log.info("Инициация доставки для заказа: {}", orderId);
        Order order = getOrderById(orderId);

        order.setState(OrderState.DELIVERED);
        orderRepository.save(order);

        return orderMapper.toOrderDto(order);
    }

    @Override
    @Transactional
    public OrderDto deliveryFailed(UUID orderId) {
        log.info("Обработка неудачной доставки для заказа: {}", orderId);
        Order order = getOrderById(orderId);

        order.setState(OrderState.DELIVERY_FAILED);
        orderRepository.save(order);

        return orderMapper.toOrderDto(order);
    }

    @Override
    @Transactional
    public OrderDto completed(UUID orderId) {
        log.info("Завершение заказа: {}", orderId);
        Order order = getOrderById(orderId);

        order.setState(OrderState.COMPLETED);
        orderRepository.save(order);

        return orderMapper.toOrderDto(order);
    }

    @Override
    @Transactional
    public OrderDto calculateTotalCost(UUID orderId) {
        log.info("Расчет общей стоимости для заказа: {}", orderId);
        Order order = getOrderById(orderId);

        BigDecimal totalPrice = paymentClient.getTotalCost(orderMapper.toOrderDto(order));
        order.setTotalPrice(totalPrice);

        orderRepository.save(order);

        return orderMapper.toOrderDto(order);
    }

    @Override
    @Transactional
    public OrderDto calculateDeliveryCost(UUID orderId) {
        log.info("Расчет стоимости доставки для заказа: {}", orderId);
        Order order = getOrderById(orderId);

        BigDecimal deliveryPrice = deliveryClient.deliveryCost(orderMapper.toOrderDto(order));
        order.setDeliveryPrice(deliveryPrice);

        orderRepository.save(order);

        return orderMapper.toOrderDto(order);
    }

    @Override
    @Transactional
    public OrderDto assembly(UUID orderId) {
        log.info("Обработка успешной сборки для заказа: {}", orderId);
        Order order = getOrderById(orderId);

        order.setState(OrderState.ASSEMBLED);
        orderRepository.save(order);

        return orderMapper.toOrderDto(order);
    }

    @Override
    @Transactional
    public OrderDto assemblyFailed(UUID orderId) {
        log.info("Обработка неудачной сборки для заказа: {}", orderId);
        Order order = getOrderById(orderId);

        order.setState(OrderState.ASSEMBLY_FAILED);
        orderRepository.save(order);

        return orderMapper.toOrderDto(order);
    }

    @Override
    @Transactional
    public OrderDto productReturn(ProductReturnRequest productReturnRequest) {
        log.info("Обработка возврата для заказа: {}", productReturnRequest.getOrderId());
        Order order = getOrderById(productReturnRequest.getOrderId());

        order.setState(OrderState.PRODUCT_RETURNED);
        orderRepository.save(order);

        return orderMapper.toOrderDto(order);
    }

    private Order getOrderById(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Заказ не найден: " + orderId));
    }
}
