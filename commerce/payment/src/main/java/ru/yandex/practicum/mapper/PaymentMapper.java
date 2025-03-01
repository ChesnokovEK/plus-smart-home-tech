package ru.yandex.practicum.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.yandex.practicum.entity.Payment;
import ru.yandex.practicum.payment.dto.PaymentDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface PaymentMapper {

    @Mapping(target = "feePayment", ignore = true)
    @Mapping(target = "deliveryPayment", ignore = true)
    PaymentDto toPaymentDto(Payment payment);
}
