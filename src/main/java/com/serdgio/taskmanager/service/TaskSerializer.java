package com.serdgio.taskmanager.service;

import com.serdgio.taskmanager.model.Priority;
import com.serdgio.taskmanager.model.Task;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

public class TaskSerializer {
    private static final String FIELD_SEPARATOR = "\t";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter
            .ofPattern("dd.MM.uuuu")
            .withResolverStyle(ResolverStyle.STRICT);

    public String toLine(Task task) {
        String title = escape(task.getTitle());
        String description = task.getDescription() == null ? "" : escape(task.getDescription());
        String createdAt = task.getCreatedAt().toString();
        String deadline = task.getDeadline() == null ? "" : task.getDeadline().format(FORMATTER);
        String priority = task.getPriority().name();

        return task.getId() + FIELD_SEPARATOR
                + title + FIELD_SEPARATOR
                + description + FIELD_SEPARATOR
                + createdAt + FIELD_SEPARATOR
                + deadline + FIELD_SEPARATOR
                + priority;
    }

    public boolean isBlankLine(String line) {
        return line == null || line.isBlank();
    }

    public boolean isDelimitedLine(String line) {
        return line != null && line.contains(FIELD_SEPARATOR);
    }

    public Task parseDelimited(String line) {
        String[] parts = line.split(FIELD_SEPARATOR, -1);
        if (parts.length != 6) return null;

        int id = Integer.parseInt(parts[0].trim());
        String title = unescape(parts[1]);
        String description = parts[2].isBlank() ? null : unescape(parts[2]);
        Instant created = Instant.parse(parts[3]);
        LocalDateTime deadline = parts[4].isBlank() ? null : LocalDate.parse(parts[4], FORMATTER).atStartOfDay();
        Priority priority = Priority.valueOf(parts[5].trim().toUpperCase());

        return new Task(id, title, description, created, deadline, priority);
    }

    public Task parseLegacy(String line) {
        String[] parts = line.trim().split("\\s+");
        if (parts.length < 4 || parts.length > 6) return null;

        int id = Integer.parseInt(parts[0].trim());
        String title = parts[1];

        String description = null;
        Instant created;
        LocalDateTime deadline = null;
        Priority priority;

        if (parts.length == 6) {
            description = parts[2];
            created = Instant.parse(parts[3]);
            deadline = LocalDate.parse(parts[4], FORMATTER).atStartOfDay();
            priority = Priority.valueOf(parts[5].trim().toUpperCase());
        } else if (parts.length == 5) {
            try {
                created = Instant.parse(parts[2]);
                deadline = LocalDate.parse(parts[3], FORMATTER).atStartOfDay();
                priority = Priority.valueOf(parts[4].trim().toUpperCase());
            } catch (Exception e) {
                description = parts[2];
                created = Instant.parse(parts[3]);
                priority = Priority.valueOf(parts[4].trim().toUpperCase());
            }
        } else {
            created = Instant.parse(parts[2]);
            priority = Priority.valueOf(parts[3].trim().toUpperCase());
        }

        return new Task(id, title, description, created, deadline, priority);
    }

    private String escape(String value) {
        return value.replace("\\", "\\\\").replace("\t", "\\t");
    }

    private String unescape(String value) {
        StringBuilder out = new StringBuilder();
        boolean escaped = false;
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            if (escaped) {
                out.append(ch == 't' ? '\t' : ch);
                escaped = false;
            } else if (ch == '\\') {
                escaped = true;
            } else {
                out.append(ch);
            }
        }
        if (escaped) out.append('\\');
        return out.toString();
    }
}