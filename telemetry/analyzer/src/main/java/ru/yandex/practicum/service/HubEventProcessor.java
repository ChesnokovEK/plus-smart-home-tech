package ru.yandex.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.AnalyzerKafkaConfig;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.time.Duration;
import java.util.Collections;

@Slf4j
@Component
public class HubEventProcessor implements Runnable {

    private final KafkaConsumer<String, HubEventAvro> hubConsumer;
    private final AnalyzerKafkaConfig kafkaConfig;

    private final SensorService sensorService;
    private final ScenarioService scenarioService;

    public HubEventProcessor(AnalyzerKafkaConfig kafkaConfig,
                             SensorService sensorService,
                             ScenarioService scenarioService) {
        this.kafkaConfig = kafkaConfig;
        hubConsumer = new KafkaConsumer<>(kafkaConfig.getHubConsumerProperties());
        this.sensorService = sensorService;
        this.scenarioService = scenarioService;
    }

    @Override
    public void run() {
        try (hubConsumer) {
            Runtime.getRuntime().addShutdownHook(new Thread(hubConsumer::wakeup));
            hubConsumer.subscribe(Collections.singletonList("telemetry.hubs.v1"));

            while (!Thread.currentThread().isInterrupted()) {
                ConsumerRecords<String, HubEventAvro> records = hubConsumer.poll(Duration.ofMillis(100));

                for (ConsumerRecord<String, HubEventAvro> record : records) {
                    try {
                        onMessage(record);
                    } catch (Exception e) {
                        log.error("Error processing event: {}", record, e);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error in Kafka consumer", e);
        }
    }

    @KafkaListener(topics = "telemetry.hubs.v1", groupId = "analyzer-hub-group")
    public void onMessage(ConsumerRecord<String, HubEventAvro> record) {
        HubEventAvro event = record.value();

        var eventPayload = event.getPayload();

        if (eventPayload instanceof DeviceAddedEventAvro deviceAddedEvent) {
            sensorService.addSensor(deviceAddedEvent.getId(), event.getHubId());
        } else if (eventPayload instanceof DeviceRemovedEventAvro deviceRemovedEvent) {
            sensorService.removeSensor(deviceRemovedEvent.getId(), event.getHubId());
        } else if (eventPayload instanceof ScenarioAddedEventAvro scenarioAddedEvent) {
            scenarioService.addScenario(scenarioAddedEvent, event.getHubId());
        } else if (eventPayload instanceof ScenarioRemovedEventAvro scenarioRemovedEvent) {
            scenarioService.deleteScenario(scenarioRemovedEvent.getName());
        } else {
            log.warn("Unknown event type: {}", event.getPayload().getClass().getName());
        }
    }
}