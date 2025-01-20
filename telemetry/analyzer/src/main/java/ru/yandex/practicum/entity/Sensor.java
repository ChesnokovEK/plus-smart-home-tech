package ru.yandex.practicum.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "sensors")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Sensor {

    @Id
    private String id;

    @Column(name = "hub_id", nullable = false)
    private String hubId;
}