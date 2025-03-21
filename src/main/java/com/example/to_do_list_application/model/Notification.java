package com.example.to_do_list_application.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Notification {

    public void setId(Long id) {
        this.id = id;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    public Notification() {}

    public Notification(String message, Task task) {
        this.message = message;
        this.task = task;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getMessage() { return message; }
    public Task getTask() { return task; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
