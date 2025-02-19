package ru.yandex.practicum.warehouse.dto;

import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DimensionDto {
    @DecimalMin(value = "1.0", message = "must be greater 0.")
    private double width;

    @DecimalMin(value = "1.0", message = "must be greater 0.")
    private double height;

    @DecimalMin(value = "1.0", message = "must be greater 0.")
    private double depth;
}
