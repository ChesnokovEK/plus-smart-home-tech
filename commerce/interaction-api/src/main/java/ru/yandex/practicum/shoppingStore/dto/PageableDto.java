package ru.yandex.practicum.shoppingStore.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "page number must be not empty")
    @Min(value = 0, message = "page number must greater or equal 0")
    private int page;

    @NotNull(message = "page size must be not empty")
    @Min(value = 1, message = "page size must greater 0")
    private int size;

    private List<String> sort;
}