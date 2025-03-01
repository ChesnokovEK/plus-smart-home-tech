package ru.yandex.practicum.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "warehouse_products")
@Data
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
