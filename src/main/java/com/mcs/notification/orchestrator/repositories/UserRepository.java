package com.mcs.notification.orchestrator.repositories;

import com.mcs.notification.orchestrator.dtos.enums.NotificationCategory;
import com.mcs.notification.orchestrator.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("""
           select distinct u
           from User u
           join u.subscribedCategories sc
           where sc = :category
           """)
    List<User> findAllByCategory(@Param("category") NotificationCategory category);
}
