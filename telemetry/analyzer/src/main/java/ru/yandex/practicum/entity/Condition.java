package ru.yandex.practicum.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "conditions")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Condition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ConditionType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation", nullable = false)
    private ConditionOperation operation;

    @Column(name = "value")
    private Integer value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scenario_id", nullable = false)
    @ToString.Exclude
    private Scenario scenario;

    @Column(name = "sensor_id", nullable = false)
    private String sensorId;
}