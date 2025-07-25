package com.mcs.notification.orchestrator.services;

import com.mcs.notification.orchestrator.dtos.NotificationRequestedDTO;
import com.mcs.notification.orchestrator.dtos.NotificationRoutedDTO;
import com.mcs.notification.orchestrator.dtos.enums.NotificationChannel;
import com.mcs.notification.orchestrator.entities.RoutingNotification;
import com.mcs.notification.orchestrator.entities.User;
import com.mcs.notification.orchestrator.repositories.RoutingNotificationRepository;
import com.mcs.notification.orchestrator.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoutingService {

    private final UserRepository userRepository;
    private final RoutingNotificationRepository routingNotificationRepository;
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchanges.delivery}")
    private String deliveryExchange;

    public void orchestrate(NotificationRequestedDTO event) {
        List<User> users = userRepository.findAllByCategory(event.category());

        for (User user : users) {

            for (NotificationChannel channel : user.getChannels()) {
                RoutingNotification task = RoutingNotification.builder()
                        .messageId(event.messageId())
                        .userId(user.getId())
                        .category(event.category())
                        .channel(channel)
                        .status("CREATED")
                        .createdAt(LocalDateTime.now())
                        .build();



                routingNotificationRepository.save(task);

                NotificationRoutedDTO notificationRoutedDTO = new NotificationRoutedDTO(
                        event.messageId(), event.category(), event.body(), user.getId(), user.getName(), channel, task.getCreatedAt()
                );
                log.info("SENT TO: {} FOR USER {} WITH ID: {}", channel.name().toLowerCase(), user.getName(), user.getId());
                rabbitTemplate.convertAndSend(deliveryExchange, channel.name().toLowerCase(), notificationRoutedDTO);
            }
        }
    }
}
