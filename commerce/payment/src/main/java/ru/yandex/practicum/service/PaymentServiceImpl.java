package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.exception.PaymentNotFoundException;
import ru.yandex.practicum.mapper.PaymentMapper;
import ru.yandex.practicum.entity.Payment;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.order.feign.OrderClient;
import ru.yandex.practicum.payment.dto.PaymentDto;
import ru.yandex.practicum.payment.enums.PaymentState;
import ru.yandex.practicum.repository.PaymentRepository;
import ru.yandex.practicum.shoppingStore.feign.ShoppingStoreClient;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final ShoppingStoreClient shoppingStoreClient;
    private final OrderClient orderClient;

    @Override
    @Transactional(readOnly = true)
    public BigDecimal productCost(OrderDto orderDto) {

        BigDecimal total = BigDecimal.ZERO;

        for (Map.Entry<UUID, Integer> entry : orderDto.getProducts().entrySet()) {
            UUID productId = entry.getKey();
            Integer quantity = entry.getValue();

            BigDecimal productPrice = shoppingStoreClient.getProduct(productId).getPrice();

            BigDecimal lineTotal = productPrice.multiply(BigDecimal.valueOf(quantity));
            total = total.add(lineTotal);
        }

        log.info("total cost {}: for order {}", orderDto.getOrderId(), total);
        return total;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalCost(OrderDto orderDto) {
        BigDecimal productTotal = productCost(orderDto);
        BigDecimal deliveryPrice = orderDto.getDeliveryPrice();

        BigDecimal vat = productTotal.multiply(BigDecimal.valueOf(0.1));

        BigDecimal total = productTotal.add(vat).add(deliveryPrice);

        log.info("total cost {}:  for order {}", orderDto.getOrderId(), total);
        return total;
    }

    @Override
    @Transactional
    public PaymentDto payment(OrderDto orderDto) {
        BigDecimal productTotal = productCost(orderDto);

        BigDecimal deliveryTotal = orderDto.getDeliveryPrice();

        BigDecimal totalPayment = getTotalCost(orderDto);

        Payment payment = Payment.builder()
                .orderId(orderDto.getOrderId())
                .productTotal(productTotal)
                .deliveryTotal(deliveryTotal)
                .totalPayment(totalPayment)
                .state(PaymentState.PENDING)
                .build();

        paymentRepository.save(payment);

        log.info("payment id {} for order {}", payment.getPaymentId(), orderDto.getOrderId());

        return paymentMapper.toPaymentDto(payment);
    }

    @Override
    @Transactional
    public void paymentSuccess(UUID paymentId) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("No payment " + paymentId));

        payment.setState(PaymentState.SUCCESS);
        paymentRepository.save(payment);

        orderClient.completed(payment.getOrderId());
    }

    @Override
    @Transactional
    public void paymentFailed(UUID paymentId) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("No payment " + paymentId));

        payment.setState(PaymentState.FAILED);
        paymentRepository.save(payment);

        orderClient.paymentFailed(payment.getOrderId());
    }
}
