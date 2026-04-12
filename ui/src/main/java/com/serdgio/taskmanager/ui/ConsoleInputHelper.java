package com.serdgio.taskmanager.ui;

import com.serdgio.taskmanager.model.Priority;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Scanner;

public final class ConsoleInputHelper {
    private static final DateTimeFormatter DEADLINE_FORMATTER = DateTimeFormatter
            .ofPattern("dd.MM.uuuu")
            .withResolverStyle(ResolverStyle.STRICT);

    private ConsoleInputHelper() {
    }

    public static String readNonBlank(String prompt, Scanner scanner) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("Value cannot be blank");
        }
    }

    public static Priority readPriority(Scanner scanner) {
        while (true) {
            System.out.print("Priority(LOW, MEDIUM, HIGH): ");
            String input = scanner.nextLine().trim();
            try {
                return Priority.valueOf(input.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid priority. Please try again.");
            }
        }
    }

    public static LocalDateTime readDeadline(Scanner scanner) {
        while (true) {
            System.out.print("Deadline in format (dd.MM.yyyy) or enter to skip: ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                return null;
            }
            try {
                return LocalDate.parse(input, DEADLINE_FORMATTER).atStartOfDay();
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date. Please try again.");
            }
        }
    }
}
