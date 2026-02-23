package com.serdgio.taskmanager.service;

import com.serdgio.taskmanager.model.Priority;
import com.serdgio.taskmanager.model.Task;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TaskService {
    List<Task> tasks = new ArrayList<>();
    Scanner scanner;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public TaskService(Scanner scanner) {
        this.scanner = scanner;
    }

    public List<Task> getTasks(){
        return tasks;
    }

    public void addTask() {
        System.out.println("Add Task");
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
}
