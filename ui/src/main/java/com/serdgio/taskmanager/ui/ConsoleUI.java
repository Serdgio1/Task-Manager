package com.serdgio.taskmanager.ui;

import com.serdgio.taskmanager.model.Priority;
import com.serdgio.taskmanager.model.Task;
import com.serdgio.taskmanager.service.LoadResult;
import com.serdgio.taskmanager.service.LoadWarning;
import com.serdgio.taskmanager.service.TaskFileRepository;
import com.serdgio.taskmanager.service.TaskRepository;
import com.serdgio.taskmanager.service.TaskService;
import com.serdgio.taskmanager.service.TaskSerializer;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class ConsoleUI {
    private final Scanner scanner = new Scanner(System.in);
    private final TaskService taskService = new TaskService();
    private final TaskRepository taskRepository = new TaskFileRepository(new TaskSerializer());

    public void start() {
        System.out.println("Welcome to the TaskManager application!");
        while (true) {
            printMenu();
            int option = readInt("Enter your option: ");

            switch (option) {
                case 1 -> addTaskUI();
                case 2 -> updateTaskUI();
                case 3 -> deleteTaskUI();
                case 4 -> showTasks(taskService.getTasks());
                case 5 -> saveTaskUI();
                case 6 -> loadTasksUI();
                case 7 -> {
                    System.out.println("Goodbye");
                    return;
                }
                default -> System.out.println("Invalid option");
            }
        }
    }

    private void printMenu() {
        System.out.println("""
                Choose an option:
                1. Add Task
                2. Update Task
                3. Delete Task
                4. View Tasks
                5. Save Task
                6. Load Tasks
                7. Exit
                """);
    }

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please try again.");
            }
        }
    }

    private void addTaskUI() {
        System.out.println("\n=== Add New Task ===");
        String name = ConsoleInputHelper.readNonBlank("Name: ", scanner);
        System.out.print("Description(enter for null): ");
        String description = scanner.nextLine();
        Priority priority = ConsoleInputHelper.readPriority(scanner);
        LocalDateTime deadline = ConsoleInputHelper.readDeadline(scanner);

        taskService.addTask(name, description, priority, deadline);
        System.out.println("Task created successfully!\n");
    }

    private void showTasks(List<Task> tasks) {
        System.out.println("Here is the list of all tasks:");
        for (Task task : tasks) {
            System.out.println(task);
        }
    }

    private void deleteTaskUI() {
        while (true) {
            int id = readInt("Please enter the id of the task you would like to delete (0 to cancel): ");

            if (id == 0) {
                break;
            }

            if (id < 1) {
                System.out.println("Invalid input. Please try again.");
                continue;
            }

            System.out.println("Are you sure you want to delete this task?");
            String answer = scanner.nextLine().trim();

            if (answer.equalsIgnoreCase("yes")) {
                boolean deleted = taskService.deleteTask(id);
                if (deleted) {
                    System.out.println("Task deleted successfully!\n");
                } else {
                    System.out.println("Task with this id does not exist.\n");
                }
                break;
            } else if (answer.equalsIgnoreCase("no")) {
                System.out.println("Deletion cancelled.\n");
                break;
            } else {
                System.out.println("Invalid input. Please try again.");
            }
        }
    }

    private void updateTaskUI() {
        while (true) {
            int id = readInt("Please enter the id of the task you would like to update(0 to cancel): ");

            if (id == 0) {
                    break;
            }

            if (id < 1) {
                System.out.println("Invalid input. Please try again.");
                continue;
            }

            System.out.println("Are you sure you want to update this task?");
            String answer = scanner.nextLine().trim();
            if (answer.equalsIgnoreCase("no")) {
                break;
            } else if (answer.equalsIgnoreCase("yes")) {
                System.out.println("Update Task");
                String name = ConsoleInputHelper.readNonBlank("Name: ", scanner);
                System.out.print("Description(enter for null): ");
                String description = scanner.nextLine();
                description = description.isBlank() ? null : description;
                Priority priority = ConsoleInputHelper.readPriority(scanner);
                LocalDateTime deadline = ConsoleInputHelper.readDeadline(scanner);
                boolean updated = taskService.updateTask(id, name, description, priority, deadline);
                if (updated) {
                    System.out.println("Task updated successfully!\n");
                } else {
                    System.out.println("Task with this id does not exist.\n");
                }
                break;
            }
        }
    }

    private void saveTaskUI() {
        System.out.println("\n=== Save Tasks ===");
        javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
        fileChooser.setDialogTitle("Save Tasks");
        fileChooser.setSelectedFile(new java.io.File("tasks.txt"));

        int result = fileChooser.showSaveDialog(null);

        if (result == javax.swing.JFileChooser.APPROVE_OPTION) {
            Path path = fileChooser.getSelectedFile().toPath().toAbsolutePath();
            try {
                taskRepository.save(path, taskService.getTasks());
                System.out.println("Tasks saved successfully!\n");
            } catch (Exception e) {
                System.out.println("Error saving tasks: " + e.getMessage() + "\n");
            }
        } else {
            System.out.println("Save cancelled.\n");
        }
    }

    private void loadTasksUI() {
        System.out.println("\n=== Load Tasks ===");
        javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
        fileChooser.setDialogTitle("Load Tasks");

        int result = fileChooser.showOpenDialog(null);

        if (result == javax.swing.JFileChooser.APPROVE_OPTION) {
            Path path = fileChooser.getSelectedFile().toPath().toAbsolutePath();
            try {
                LoadResult loadResult = taskRepository.load(path);
                taskService.replaceAllTasks(loadResult.tasks());
                for (LoadWarning warning : loadResult.warnings()) {
                    System.out.println("Warning [" + warning.code() + "]: " + warning.message()
                            + " Line: " + warning.sourceLine());
                }
                System.out.println("Tasks loaded successfully!\n");
            } catch (Exception e) {
                System.out.println("Error loading tasks: " + e.getMessage() + "\n");
            }
        } else {
            System.out.println("Load cancelled.\n");
        }
    }
}
