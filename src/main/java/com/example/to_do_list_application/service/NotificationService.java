package com.example.to_do_list_application.service;

import com.example.to_do_list_application.model.Notification;
import com.example.to_do_list_application.model.Task;
import com.example.to_do_list_application.repository.NotificationRepository;
import com.example.to_do_list_application.repository.TaskRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private final TaskRepository taskRepository;
    private final NotificationRepository notificationRepository;

    public NotificationService(TaskRepository taskRepository, NotificationRepository notificationRepository) {
        this.taskRepository = taskRepository;
        this.notificationRepository = notificationRepository;
    }

    // Run every hour to check for notifications
    @Scheduled(fixedRate = 3600000)
    public void checkUpcomingAndOverdueTasks() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime upcomingThreshold = now.plusDays(1); // Notify 1 day before

        List<Task> tasks = taskRepository.findAll();
        for (Task task : tasks) {
            if (task.getDueDate() != null) {
                if (task.getDueDate().isBefore(now) && !task.getStatus().equals("Completed")) {
                    createNotification("Task is overdue!", task);
                } else if (task.getDueDate().isBefore(upcomingThreshold)) {
                    createNotification("Task is due soon!", task);
                }
            }
        }
    }

    public void createNotification(String message, Task task) {
        Notification notification = new Notification(message, task);
        notificationRepository.save(notification);
    }

    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }
}
