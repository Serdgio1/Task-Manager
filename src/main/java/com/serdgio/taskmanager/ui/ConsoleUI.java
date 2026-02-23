package com.serdgio.taskmanager.ui;

import com.serdgio.taskmanager.model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConsoleUI {
    private final Scanner scanner = new Scanner(System.in);

    public void start() {
        List<Task> tasks = new ArrayList<>();
        System.out.println("Welcome to the TaskManager application!");
        while (true) {
            printMenu();
            int option = readInt("Enter your option: ");

            switch (option) {
                case 1 -> System.out.println("Welcome to the TaskManager application!");
                case 2 -> System.out.println("Welcome to the TaskManager application!");
                case 3 -> System.out.println("Welcome to the TaskManager application!");
                case 4 -> showTasks(tasks);
                case 5 -> System.out.println("Welcome to the TaskManager application!");
                case 6 -> {
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

    private void showTasks(List<Task> tasks) {
        System.out.println("Here is the list of all tasks:");
        for (Task task : tasks) {
            System.out.println(task);
        }
    }
}
