package com.mcs.notification.orchestrator.entities;

import com.mcs.notification.orchestrator.dtos.enums.NotificationCategory;
import com.mcs.notification.orchestrator.dtos.enums.NotificationChannel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    @Id
    @Column(name = "user_id")
    private Long id;

    @Column(name = "user_name")
    private String name;

    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "device_token")
    private String deviceToken;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_subscriptions", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "category")
    @Enumerated(EnumType.STRING)
    private List<NotificationCategory> subscribedCategories;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_channels", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "channel")
    @Enumerated(EnumType.STRING)
    private List<NotificationChannel> channels;
}
