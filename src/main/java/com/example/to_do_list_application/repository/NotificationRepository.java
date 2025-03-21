package com.example.to_do_list_application.repository;

import com.example.to_do_list_application.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
