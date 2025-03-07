package ru.yandex.practicum.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)

    @Column(nullable = false)
    private UUID shoppingCartId;

    @ElementCollection
    @CollectionTable(name = "booking_products", joinColumns = @JoinColumn(name = "booking_id"))
    @MapKeyColumn(name = "product_id")
    @Column(name = "quantity")
    private Map<UUID, Integer> products;

    @Column(nullable = false)
    private Double deliveryWeight;

    @Column(nullable = false)
    private Double deliveryVolume;

    @Column(nullable = false)
    private Boolean fragile;
}
