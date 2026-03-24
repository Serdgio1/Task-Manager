package com.serdgio.taskmanager.service;

import com.serdgio.taskmanager.model.Priority;
import com.serdgio.taskmanager.model.Task;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class TaskService {
    private final List<Task> tasks = new ArrayList<>();
    private int nextId = 1;

    public TaskService() {}

    public List<Task> getTasks() {
        return Collections.unmodifiableList(tasks);
    }

    public Optional<Task> findById(int id) {
        for (Task task : tasks) {
            if (task.getId() == id) {
                return Optional.of(task);
            }
        }
        return Optional.empty();
    }

    public void addTask(String name, String description, Priority priority, LocalDateTime deadline) {
        String normalizedName = normalizeName(name);
        String normalizedDescription = normalizeDescription(description);
        Priority normalizedPriority = normalizePriority(priority);
        Instant created = Instant.now().truncatedTo(ChronoUnit.DAYS);
        tasks.add(new Task(nextId++, normalizedName, normalizedDescription, created, deadline, normalizedPriority));
    }

    public boolean deleteTask(int id) {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getId() == id) {
                tasks.remove(i);
                return true;
            }
        }
        return false;
    }

    public boolean updateTask(int id,  String name, String description, Priority priority, LocalDateTime deadline) {
        String normalizedName = normalizeName(name);
        String normalizedDescription = normalizeDescription(description);
        Priority normalizedPriority = normalizePriority(priority);
        for (Task task : tasks) {
            if (id == task.getId())  {
                task.setTitle(normalizedName);
                task.setDescription(normalizedDescription);
                task.setDeadline(deadline);
                task.setPriority(normalizedPriority);
                return true;
            }
        }
        return false;
    }

    private String normalizeName(String name) {
        String normalizedName = name == null ? "" : name.trim();
        if (normalizedName.isEmpty()) {
            throw new IllegalArgumentException("Task name cannot be blank");
        }
        return normalizedName;
    }

    private String normalizeDescription(String description) {
        return (description == null || description.isBlank()) ? null : description.trim();
    }

    private Priority normalizePriority(Priority priority) {
        return priority == null ? Priority.MEDIUM : priority;
    }

    public void replaceAllTasks(List<Task> loadedTasks) {
        if (loadedTasks == null) {
            throw new IllegalArgumentException("Loaded tasks cannot be null");
        }

        Set<Integer> uniqueIds = new HashSet<>();
        for (Task task : loadedTasks) {
            if (task == null) {
                throw new IllegalArgumentException("Loaded tasks cannot contain null elements");
            }
            if (!uniqueIds.add(task.getId())) {
                throw new IllegalArgumentException("Loaded tasks contain duplicate id: " + task.getId());
            }
        }

        tasks.clear();
        tasks.addAll(loadedTasks);

        int maxId = 0;
        for (Task task : tasks) {
            if (task.getId() > maxId) {
                maxId = task.getId();
            }
        }
        nextId = maxId + 1;
    }
}
