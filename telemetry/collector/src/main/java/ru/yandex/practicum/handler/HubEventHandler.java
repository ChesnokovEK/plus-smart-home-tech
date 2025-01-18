package ru.yandex.practicum.handler;

import ru.yandex.practicum.model.HubEvent;
import ru.yandex.practicum.model.HubEventType;

public interface HubEventHandler {
    HubEventType getMessageType();
    void handle (HubEvent event);
}
