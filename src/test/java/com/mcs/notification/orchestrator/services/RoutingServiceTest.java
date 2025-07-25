package com.mcs.notification.orchestrator.services;

import com.mcs.notification.orchestrator.dtos.NotificationRequestedDTO;
import com.mcs.notification.orchestrator.dtos.NotificationRoutedDTO;
import com.mcs.notification.orchestrator.dtos.enums.NotificationCategory;
import com.mcs.notification.orchestrator.dtos.enums.NotificationChannel;
import com.mcs.notification.orchestrator.entities.RoutingNotification;
import com.mcs.notification.orchestrator.entities.User;
import com.mcs.notification.orchestrator.repositories.RoutingNotificationRepository;
import com.mcs.notification.orchestrator.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RoutingServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoutingNotificationRepository routingNotificationRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private RoutingService routingService;


    @Test
    void orchestrate_should_create_and_send_for_each_user_and_channel() {
        NotificationRequestedDTO event = new NotificationRequestedDTO(
                1L, NotificationCategory.MOVIES, "Movie", LocalDateTime.now()
        );
        User user1 = User.builder().id(10L).name("Alice").channels(List.of(NotificationChannel.EMAIL, NotificationChannel.SMS)).build();
        User user2 = User.builder().id(20L).name("Bob").channels(List.of(NotificationChannel.PUSH)).build();

        when(userRepository.findAllByCategory(NotificationCategory.MOVIES)).thenReturn(List.of(user1, user2));


        routingService.orchestrate(event);

        verify(routingNotificationRepository, times(3)).save(any(RoutingNotification.class));

        ArgumentCaptor<String> exchangeCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> routingKeyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<NotificationRoutedDTO> dtoCaptor = ArgumentCaptor.forClass(NotificationRoutedDTO.class);

        verify(rabbitTemplate, times(3))
                .convertAndSend(exchangeCaptor.capture(), routingKeyCaptor.capture(), dtoCaptor.capture());

        List<String> routingKeys = routingKeyCaptor.getAllValues();
        assertTrue(routingKeys.contains("email"));
        assertTrue(routingKeys.contains("sms"));
        assertTrue(routingKeys.contains("push"));

        for (NotificationRoutedDTO dto : dtoCaptor.getAllValues()) {
            assertEquals(1L, dto.messageId());
            assertEquals(NotificationCategory.MOVIES, dto.category());
            assertTrue(List.of("Alice", "Bob").contains(dto.userName()));
            assertTrue(List.of("email", "sms", "push").contains(dto.channel().name().toLowerCase()));
            assertNotNull(dto.createdAt());
        }
    }

    @Test
    void orchestrate_when_no_users_then_does_nothing() {
        NotificationRequestedDTO event = new NotificationRequestedDTO(1L,NotificationCategory.MOVIES,"body", LocalDateTime.now());
        when(userRepository.findAllByCategory(NotificationCategory.MOVIES)).thenReturn(List.of());

        routingService.orchestrate(event);

        verifyNoInteractions(routingNotificationRepository);
        verifyNoInteractions(rabbitTemplate);
    }
}
