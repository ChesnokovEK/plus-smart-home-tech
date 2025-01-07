package ru.yandex.practicum.service.handler;


import org.springframework.stereotype.Service;
import ru.yandex.practicum.configuration.KafkaConfig;
import ru.yandex.practicum.handler.BaseSensorEventHandler;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;
import ru.yandex.practicum.model.SensorEvent;
import ru.yandex.practicum.model.SensorEventType;
import ru.yandex.practicum.model.TemperatureSensorEvent;

@Service
public class TemperatureSensorEventHandler extends BaseSensorEventHandler<TemperatureSensorAvro> {

    public TemperatureSensorEventHandler(KafkaConfig.KafkaEventProducer producer, KafkaConfig kafkaTopics) {
        super(producer, kafkaTopics);
    }

    @Override
    public SensorEventType getMessageType() {
        return SensorEventType.TEMPERATURE_SENSOR_EVENT;
    }

    @Override
    protected TemperatureSensorAvro mapToAvro(SensorEvent event) {
        var tempEvent = (TemperatureSensorEvent) event;

        return new TemperatureSensorAvro(
                tempEvent.getId(),
                tempEvent.getHubId(),
                tempEvent.getTimestamp().toEpochMilli(),
                tempEvent.getTemperatureC(),
                tempEvent.getTemperatureF()
        );
    }
}
