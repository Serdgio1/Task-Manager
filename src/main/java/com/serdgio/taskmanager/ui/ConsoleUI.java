package com.serdgio.taskmanager.ui;

import com.serdgio.taskmanager.model.Task;
import com.serdgio.taskmanager.service.TaskService;

import java.util.List;
import java.util.Scanner;

public class ConsoleUI {
    private final Scanner scanner = new Scanner(System.in);
    TaskService taskService = new TaskService(scanner);

    public void start() {
        System.out.println("Welcome to the TaskManager application!");
        while (true) {
            printMenu();
            int option = readInt();

            switch (option) {
                case 1 -> taskService.addTask();
                case 2 -> System.out.println("Welcome to the TaskManager application!");
                case 3 -> System.out.println("Welcome to the TaskManager application!");
                case 4 -> showTasks(taskService.getTasks());
                case 5 -> System.out.println("Welcome to the TaskManager application!");
                case 6 -> System.out.println("Welcome to the TaskManager application!");
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
                6. Edit Task
                7. Exit
                """);
    }

    private int readInt() {
        while (true) {
            System.out.print("Enter your option: ");
            String input = scanner.nextLine().trim();

            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please try again.");
            }
        }
    }

    private void showTasks(List<Task> tasks) {
        System.out.println("Here is the list of all tasks:");
        for (Task task : tasks) {
            System.out.println(task);
        }
    }

    private void deleteTaskUI() {
        while (true) {
            System.out.println("Please enter the id of the task you would like to delete:");
            int id = readInt();
            if (id > taskService.getTasks().size() || id < 1) {
                System.out.println("Invalid input. Please try again.");
                continue;
            }
            System.out.println("Are you sure you want to delete this task?");
            String answer = scanner.nextLine().trim();
            if (answer.equalsIgnoreCase("yes")) {
                taskService.deleteTask(id);
                break;
            } else if (answer.equalsIgnoreCase("no")) {
                break;
            } else {
                System.out.println("Invalid input. Please try again.");
            }
        }
    }

}
