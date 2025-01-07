package ru.yandex.practicum.service.handler;


import org.springframework.stereotype.Service;

import ru.yandex.practicum.configuration.KafkaConfig;
import ru.yandex.practicum.handler.BaseHubEventHandler;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.model.HubEvent;
import ru.yandex.practicum.model.HubEventType;
import ru.yandex.practicum.model.ScenarioRemovedEvent;

@Service
public class ScenarioRemovedEventHandler extends BaseHubEventHandler<ScenarioRemovedEventAvro> {

    public ScenarioRemovedEventHandler(KafkaConfig.KafkaEventProducer producer, KafkaConfig kafkaTopics) {
        super(producer, kafkaTopics);
    }

    @Override
    public HubEventType getMessageType() {
        return HubEventType.SCENARIO_REMOVED;
    }

    @Override
    protected ScenarioRemovedEventAvro mapToAvro(HubEvent event) {
        var scenarioRemovedEvent = (ScenarioRemovedEvent) event;

        return new ScenarioRemovedEventAvro(scenarioRemovedEvent.getName());

    }
}
