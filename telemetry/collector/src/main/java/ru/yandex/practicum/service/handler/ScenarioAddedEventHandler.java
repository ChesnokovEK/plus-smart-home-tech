package ru.yandex.practicum.service.handler;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.configuration.KafkaConfig;
import ru.yandex.practicum.handler.BaseHubEventHandler;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.model.*;

@Service
public class ScenarioAddedEventHandler extends BaseHubEventHandler<ScenarioAddedEventAvro> {

    public ScenarioAddedEventHandler(KafkaConfig.KafkaEventProducer producer, KafkaConfig kafkaTopics) {
        super(producer, kafkaTopics);
    }

    @Override
    public HubEventType getMessageType() {
        return HubEventType.SCENARIO_ADDED;
    }

    @Override
    protected ScenarioAddedEventAvro mapToAvro(HubEvent event) {
        var scenarioEvent = (ScenarioAddedEvent) event;

        return new ScenarioAddedEventAvro(
                scenarioEvent.getName(),
                scenarioEvent.getConditions().stream().map(this::mapConditionToAvro).toList(),
                scenarioEvent.getActions().stream().map(this::mapActionToAvro).toList()
        );
    }

    private ScenarioConditionAvro mapConditionToAvro(ScenarioCondition condition) {
        return new ScenarioConditionAvro(
                condition.getSensorId(),
                ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro.valueOf(condition.getType().name()),
                ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro.valueOf(condition.getOperation().name()),
                condition.getValue()
        );
    }

    private DeviceActionAvro mapActionToAvro(DeviceAction action) {
        return new DeviceActionAvro(
                action.getSensorId(),
                ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro.valueOf(action.getType().name()),
                action.getValue()
        );
    }
}
