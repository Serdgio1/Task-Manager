package com.serdgio.taskmanager.model;

import com.serdgio.taskmanager.service.TaskService;

import java.time.Instant;
import java.time.LocalDateTime;

public class Task {
    private int id = 0;
    private String title;
    private String description;
    TaskSatus satus;
    Instant createdAt;
    LocalDateTime deadline;
    Priority priority;

    public Task(String title, String description, Instant createdAt, LocalDateTime deadline, Priority priority) {
        this.id = ++id;
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
        this.deadline = deadline;
        this.priority = priority;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        TaskService tS = new TaskService();
        LocalDateTime now = tS.formatDate(this.createdAt);
        return this.id + " " + this.title + " " + this.description + " " + now + " " + this.deadline + " " +  this.priority;
    }
}
