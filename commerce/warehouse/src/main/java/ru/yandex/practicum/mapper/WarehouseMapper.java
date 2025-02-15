package ru.yandex.practicum.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.yandex.practicum.entity.WarehouseProduct;
import ru.yandex.practicum.warehouse.dto.NewProductInWarehouseRequest;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = {DimensionMapper.class}
)
public interface WarehouseMapper {

    @Mapping(target = "quantityAvailable", constant = "0")
    WarehouseProduct toWarehouseProduct(NewProductInWarehouseRequest request);
}
