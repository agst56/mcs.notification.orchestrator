package com.mcs.notification.orchestrator.listeners;

import com.mcs.notification.orchestrator.dtos.NotificationRequestedDTO;
import com.mcs.notification.orchestrator.services.RoutingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationRequestedListener {

    private final RoutingService orchestratorService;

    @RabbitListener(queues = "${rabbitmq.queues.notification-requested}")
    public void handleNotificationRequested(NotificationRequestedDTO event) {
        log.info("the event {} with body {}", event.messageId(), event.body());
        orchestratorService.orchestrate(event);
    }
}
