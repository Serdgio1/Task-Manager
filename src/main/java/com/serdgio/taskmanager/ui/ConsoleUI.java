package com.serdgio.taskmanager.ui;

import com.serdgio.taskmanager.model.Task;
import com.serdgio.taskmanager.service.TaskService;

import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

public class ConsoleUI {
    private final Scanner scanner = new Scanner(System.in);
    TaskService taskService = new TaskService(scanner);

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
                6. Exit
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
        taskService.addTask();
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

            if (id > taskService.getTasks().size() || id < 1) {
                System.out.println("Invalid input. Please try again.");
                continue;
            }

            System.out.println("Are you sure you want to delete this task?");
            String answer = scanner.nextLine().trim();

            if (answer.equalsIgnoreCase("yes")) {
                taskService.deleteTask(id);
                System.out.println("Task deleted successfully!\n");
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

            if (id > taskService.getTasks().size() || id < 1) {
                System.out.println("Invalid input. Please try again.");
                continue;
            }

            System.out.println("Are you sure you want to update this task?");
            String answer = scanner.nextLine().trim();
            if (answer.equalsIgnoreCase("no")) {
                break;
            } else if (answer.equalsIgnoreCase("yes")) {
                taskService.updateTask(id);
                System.out.println("Task updated successfully!\n");
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
                taskService.saveTasks(path);
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
                taskService.loadTasks(path);
                System.out.println("Tasks loaded successfully!\n");
            } catch (Exception e) {
                System.out.println("Error loading tasks: " + e.getMessage() + "\n");
            }
        } else {
            System.out.println("Load cancelled.\n");
        }
    }
}
