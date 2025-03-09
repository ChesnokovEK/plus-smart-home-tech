package ru.yandex.practicum.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "warehouse_products")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseProduct {

    @Id
    private UUID productId;

    @Column(nullable = false)
    private boolean fragile;

    @Embedded
    private Dimension dimension;

    @Column(nullable = false)
    private double weight;

    @Column(nullable = false)
    private int quantityAvailable;
}
