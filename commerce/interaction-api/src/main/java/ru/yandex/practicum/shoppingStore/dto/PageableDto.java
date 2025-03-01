package ru.yandex.practicum.shoppingStore.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageableDto {

    @PositiveOrZero
    private int page;

    @Positive
    private int size;

    private List<String> sort;
}