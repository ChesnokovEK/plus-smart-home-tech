package ru.yandex.practicum.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.yandex.practicum.delivery.dto.AddressDto;
import ru.yandex.practicum.entity.Address;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface AddressMapper {

    @Mapping(target = "addressId", ignore = true)
    Address fromAddressDto(AddressDto addressDto);

    AddressDto toAddressDto(Address address);
}
