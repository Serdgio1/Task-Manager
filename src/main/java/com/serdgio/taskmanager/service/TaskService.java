package com.serdgio.taskmanager.service;

import com.serdgio.taskmanager.model.Priority;
import com.serdgio.taskmanager.model.Task;
import com.serdgio.taskmanager.ui.ConsoleInputHelper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TaskService {
    List<Task> tasks = new ArrayList<>();
    Scanner scanner;
    private static final DateTimeFormatter formatter = DateTimeFormatter
            .ofPattern("dd.MM.uuuu")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final String FIELD_SEPARATOR = "\t";

    public TaskService() {}

    public TaskService(Scanner scanner) {
        this.scanner = scanner;
    }

    public List<Task> getTasks(){
        return tasks;
    }

    public void addTask(String name, String description, Priority priority, LocalDateTime deadline) {
        String normalizedName = name == null ? "" : name.trim();
        if (normalizedName.isEmpty()) {
            throw new IllegalArgumentException("Task name cannot be blank");
        }

        String normalizedDescription = (description == null || description.isBlank()) ? null : description.trim();
        Priority normalizedPriority = priority == null ? Priority.MEDIUM : priority;
        Instant created = Instant.now().truncatedTo(ChronoUnit.DAYS);
        tasks.add(new Task(normalizedName, normalizedDescription, created, deadline, normalizedPriority));
    }

    public void addTask(String name, String description, LocalDate deadline) {
        LocalDateTime deadlineDateTime = deadline == null ? null : deadline.atStartOfDay();
        addTask(name, description, Priority.MEDIUM, deadlineDateTime);
    }


    public LocalDateTime formatDate(Instant input) {
        try {
            return LocalDateTime.ofInstant(input, ZoneId.systemDefault());
        } catch (DateTimeParseException e) {
        System.out.println("Invalid date. Please try again.");
        }
        return null;
    }

    public void deleteTask(int id) {
        int indexToRemove = -1;
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getId() == id) {
                indexToRemove = i;
                break;
            }
        }

        if (indexToRemove == -1) {
            return;
        }

        Task removedTask = tasks.remove(indexToRemove);
        for (int i = 0; i < tasks.size(); i++) {
            tasks.get(i).setId(i + 1);
        }
        removedTask.setIdCounter(tasks.size());
    }

    public void updateTask(int id) {
        System.out.println("Update Task");
        String name = ConsoleInputHelper.readNonBlank("Name: ", scanner);
        System.out.print("Description(enter for null): ");
        String description = scanner.nextLine();
        description = description.isBlank() ? null : description;
        Priority priority = ConsoleInputHelper.readPriority(scanner);
        LocalDateTime deadline = ConsoleInputHelper.readDeadline(scanner);

        for (Task task : tasks) {
            if (id == task.getId())  {
                task.setTitle(name);
                task.setDescription(description);
                task.setDeadline(deadline);
                task.setPriority(priority);
            }
        }
    }

    private String extractTaskData() {
        StringBuilder data = new StringBuilder();
        for (Task task : tasks) {
            String title = escapeField(task.getTitle());
            String description = task.getDescription() == null ? "" : escapeField(task.getDescription());
            String createdAt = task.getCreatedAt().toString();
            String deadline = task.getDeadline() == null ? "" : task.getDeadline().format(formatter);
            String priority = task.getPriority().name();

            data.append(task.getId()).append(FIELD_SEPARATOR)
                    .append(title).append(FIELD_SEPARATOR)
                    .append(description).append(FIELD_SEPARATOR)
                    .append(createdAt).append(FIELD_SEPARATOR)
                    .append(deadline).append(FIELD_SEPARATOR)
                    .append(priority)
                    .append("\n");
        }
        return data.toString();
    }

    private String escapeField(String value) {
        return value.replace("\\", "\\\\").replace("\t", "\\t");
    }

    private String unescapeField(String value) {
        StringBuilder out = new StringBuilder();
        boolean escaped = false;
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            if (escaped) {
                if (ch == 't') {
                    out.append('\t');
                } else {
                    out.append(ch);
                }
                escaped = false;
            } else if (ch == '\\') {
                escaped = true;
            } else {
                out.append(ch);
            }
        }
        if (escaped) {
            out.append('\\');
        }
        return out.toString();
    }

    public void saveTasks(Path path) throws IOException {
        String tasksString = extractTaskData();
        Files.writeString(path, tasksString, StandardCharsets.UTF_8);
    }

    public void loadTasks(Path path) throws IOException {
        tasks.clear();
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

        for (String line : lines) {
            if (line == null || line.isBlank()) {
                continue;
            }

            try {
                Task task = line.contains(FIELD_SEPARATOR)
                        ? parseDelimitedLine(line)
                        : parseLegacyLine(line);

                if (task != null) {
                    tasks.add(task);
                }
            } catch (RuntimeException e) {
                System.out.println("Skipping malformed task line: " + line + " (" + e.getMessage() + ")");
            }
        }
    }

    private Task parseDelimitedLine(String line) {
        String[] parts = line.split(FIELD_SEPARATOR, -1);
        if (parts.length != 6) {
            return null;
        }

        String title = unescapeField(parts[1]);
        String description = parts[2].isBlank() ? null : unescapeField(parts[2]);
        Instant created = Instant.parse(parts[3]);
        LocalDateTime deadline = parts[4].isBlank() ? null : LocalDate.parse(parts[4], formatter).atStartOfDay();
        Priority priority = Priority.valueOf(parts[5].trim().toUpperCase());

        return new Task(title, description, created, deadline, priority);
    }

    private Task parseLegacyLine(String line) {
        String[] parts = line.trim().split("\\s+");

        String title;
        String description;
        Instant created;
        LocalDateTime deadline;
        Priority priority;

        if (parts.length == 6) {
            title = parts[1];
            description = parts[2];
            created = Instant.parse(parts[3]);
            deadline = LocalDate.parse(parts[4], formatter).atStartOfDay();
            priority = Priority.valueOf(parts[5].trim().toUpperCase());
        } else if (parts.length == 5) {
            title = parts[1];
            try {
                created = Instant.parse(parts[2]);
                deadline = LocalDate.parse(parts[3], formatter).atStartOfDay();
                priority = Priority.valueOf(parts[4].trim().toUpperCase());
                description = null;
            } catch (DateTimeParseException e) {
                description = parts[2];
                created = Instant.parse(parts[3]);
                priority = Priority.valueOf(parts[4].trim().toUpperCase());
                deadline = null;
            }
        } else if (parts.length == 4) {
            title = parts[1];
            created = Instant.parse(parts[2]);
            priority = Priority.valueOf(parts[3].trim().toUpperCase());
            description = null;
            deadline = null;
        } else {
            return null;
        }

        return new Task(title, description, created, deadline, priority);
    }
}
