package com.serdgio.taskmanager.model;

import com.serdgio.taskmanager.service.TaskService;

import java.time.Instant;
import java.time.LocalDateTime;
public class Task {
    private int id;
    private static int idCounter = 0;
    private String title;
    private String description;
    TaskSatus satus;
    Instant createdAt;
    LocalDateTime deadline;
    Priority priority;

    public Task(String title, String description, Instant createdAt, LocalDateTime deadline, Priority priority) {
        this.id = ++idCounter;
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
        this.deadline = deadline;
        this.priority = priority;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public Priority getPriority() {
        return priority;
    }

    public int getIdCounter() {
        return idCounter;
    }

    public void setIdCounter(int idCounter) {
        Task.idCounter = idCounter;
    }
    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        TaskService tS = new TaskService();
        LocalDateTime now = tS.formatDate(this.createdAt);
        return this.id + " " + this.title + " " + this.description + " " + now + " " + this.deadline + " " +  this.priority;
    }
}
