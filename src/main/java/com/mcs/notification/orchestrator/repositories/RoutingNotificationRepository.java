package com.mcs.notification.orchestrator.repositories;


import com.mcs.notification.orchestrator.entities.RoutingNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoutingNotificationRepository extends JpaRepository<RoutingNotification, Long> {
}
