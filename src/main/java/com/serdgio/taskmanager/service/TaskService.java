package com.serdgio.taskmanager.service;

import com.serdgio.taskmanager.model.Priority;
import com.serdgio.taskmanager.model.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TaskService {
    List<Task> tasks = new ArrayList<>();
    Scanner scanner;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public TaskService() {}

    public TaskService(Scanner scanner) {
        this.scanner = scanner;
    }

    public List<Task> getTasks(){
        return tasks;
    }

    public void addTask() {
        String name = nonBlank("Name: ");
        System.out.print("Description(enter for null): ");
        String description = scanner.nextLine();
        description = description.isBlank() ? null : description;
        Priority priority = readPriority();
        LocalDateTime deadline = date();
        Instant created = Instant.now();
        Task task = new Task(name, description, created, deadline, priority);
        tasks.add(task);
    }

    private String nonBlank(String input) {
        while (true) {
            System.out.print(input);
            input = scanner.nextLine();
            if (input.isEmpty()) {
                System.out.println("Value cannot be blank");
                continue;
            }
            return input.trim();
        }
    }

    private Priority readPriority() {
        while (true) {
            System.out.print("Priority(LOW, MEDIUM, HIGH): ");
            String input = scanner.nextLine();

            try {
                return Priority.valueOf(input.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid priority. Please try again.");
            }
        }
    }

    private LocalDateTime date() {
        while (true) {
            System.out.print("Deadline in format (dd.MM.yyyy) or enter to skip: ");
            String input = scanner.nextLine();
            if (input.isEmpty()) {
                return null;
            }
            try {
                return LocalDateTime.parse(input.trim(), formatter);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date. Please try again.");
            }
        }
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
        boolean f = false;
        int index = 0;
        for (int i = 0; i < tasks.size(); i++) {
            if (id == tasks.get(i).getId())  {
                index = i;
                f = true;
                continue;
            }

            if (f) {
                tasks.get(i).setId(tasks.get(i).getId() - 1);
                tasks.get(i).setIdCounter(tasks.get(i).getIdCounter() - 1);
            }
        }
        tasks.remove(index);
    }

    public void updateTask(int id) {
        System.out.println("Update Task");
        String name = nonBlank("Name: ");
        System.out.print("Description(enter for null): ");
        String description = scanner.nextLine();
        description = description.isBlank() ? null : description;
        Priority priority = readPriority();
        LocalDateTime deadline = date();

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
        String data = "";
        for (Task task : tasks) {
            data += task.getId() + ". " + task.getTitle() + " " + task.getDescription() + " " + task.getCreatedAt().toString();
            if (task.getDeadline() != null) {
                data += " " + task.getDeadline().format(formatter);
            } else {
                data += " ";
            }
            data += " " + task.getPriority().name() + "\n";
        }
        return data;
    }

    public void saveTasks(Path path) throws IOException {
        String tasksString = extractTaskData();
        Files.writeString(path, tasksString, StandardCharsets.UTF_8);
    }

    public void loadTasks(Path path) throws IOException {
        tasks.clear();
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        for (String line : lines) {
            String[] parts = line.split(" ", 5);
            int id = Integer.parseInt(parts[0].replace(".", ""));
            String title = parts[1];
            String description = parts[2].equals("null") ? null : parts[2];
            Instant createdAt = Instant.parse(parts[3]);
            LocalDateTime deadline = null;
            if (!parts[4].isBlank()) {
                deadline = LocalDateTime.parse(parts[4], formatter);
            }
            Priority priority = Priority.valueOf(parts[5]);
            Task task = new Task(title, description, createdAt, deadline, priority);
            tasks.add(task);
        }
    }
}
