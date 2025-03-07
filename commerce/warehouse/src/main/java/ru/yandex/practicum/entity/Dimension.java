package ru.yandex.practicum.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class Dimension {
    private double width;
    private double height;
    private double depth;
}
