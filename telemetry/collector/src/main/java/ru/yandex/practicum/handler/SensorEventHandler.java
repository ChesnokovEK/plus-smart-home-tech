package ru.yandex.practicum.handler;

import ru.yandex.practicum.model.SensorEvent;
import ru.yandex.practicum.model.SensorEventType;

public interface SensorEventHandler {
    SensorEventType getMessageType();
    void handle(SensorEvent event);
}
