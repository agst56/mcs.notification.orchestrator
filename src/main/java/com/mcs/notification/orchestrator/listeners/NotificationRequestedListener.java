package com.mcs.notification.orchestrator.listeners;

import com.mcs.notification.orchestrator.dtos.NotificationRequestedDTO;
import com.mcs.notification.orchestrator.services.RoutingService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationRequestedListener {

    private static final Logger log = LoggerFactory.getLogger(NotificationRequestedListener.class);
    private final RoutingService orchestratorService;

    @RabbitListener(queues = "${rabbitmq.queues.notification-requested}")
    public void handleNotificationRequested(NotificationRequestedDTO event) {
        log.info("the event {} with body {}", event.messageId(), event.body());
        orchestratorService.orchestrate(event);
    }
}
