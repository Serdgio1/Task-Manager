package com.serdgio.taskmanager.model;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Task {
    private int id;
    private String title;
    private String description;
    private Instant createdAt;
    private LocalDateTime deadline;
    private Priority priority;

    private static final DateTimeFormatter DISPLAY_DATE_TIME = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public Task(int id, String title, String description, Instant createdAt, LocalDateTime deadline, Priority priority) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
        this.deadline = deadline;
        this.priority = priority;
    }

    public Task(String title, String description, Instant createdAt, LocalDateTime deadline, Priority priority) {
        this(0, title, description, createdAt, deadline, priority);
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

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        LocalDateTime createdLocal = LocalDateTime.ofInstant(this.createdAt, ZoneId.systemDefault());
        String createdText = createdLocal.format(DISPLAY_DATE_TIME);
        String deadlineText = this.deadline == null ? "-" : this.deadline.format(DISPLAY_DATE_TIME);
        return this.id + " " + this.title + " " + this.description + " " + createdText + " " + deadlineText + " " + this.priority;
    }
}
