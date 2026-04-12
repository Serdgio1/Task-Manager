package com.serdgio.taskmanager.service;

import com.serdgio.taskmanager.model.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * File-based implementation of {@link TaskRepository}.
 * Uses {@link TaskSerializer} to convert tasks to/from text lines.
 */
public class TaskFileRepository implements TaskRepository {
    private final TaskSerializer serializer;

    /**
     * @param serializer serializer used for task line parsing/formatting
     */
    public TaskFileRepository(TaskSerializer serializer) {
        this.serializer = serializer;
    }

    /**
     * Writes tasks as UTF-8 text file, one line per task.
     */
    @Override
    public void save(Path path, List<Task> tasks) throws IOException {
        StringBuilder data = new StringBuilder();
        for (Task task : tasks) {
            data.append(serializer.toLine(task)).append("\n");
        }
        Files.writeString(path, data.toString(), StandardCharsets.UTF_8);
    }

    /**
     * Reads tasks from UTF-8 text file and returns parsed tasks plus warnings.
     * Invalid lines are skipped and represented as {@link LoadWarning}.
     */
    @Override
    public LoadResult load(Path path) throws IOException {
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        List<Task> loaded = new ArrayList<>();
        List<LoadWarning> warnings = new ArrayList<>();

        for (String line : lines) {
            if (serializer.isBlankLine(line)) {
                continue;
            }

            try {
                Task task;
                if (serializer.isDelimitedLine(line)) {
                    task = serializer.parseDelimited(line);
                } else {
                    warnings.add(new LoadWarning(
                            LoadWarning.Code.LEGACY_FORMAT,
                            "Legacy task format detected. Only strict legacy lines are supported.",
                            line
                    ));
                    task = serializer.parseLegacy(line);
                    if (task == null) {
                        warnings.add(new LoadWarning(
                                LoadWarning.Code.UNSUPPORTED_LEGACY_LINE,
                                "Skipping unsupported legacy line.",
                                line
                        ));
                    }
                }

                if (task != null) {
                    loaded.add(task);
                }
            } catch (RuntimeException e) {
                warnings.add(new LoadWarning(
                        LoadWarning.Code.MALFORMED_LINE,
                        "Skipping malformed task line: " + e.getMessage(),
                        line
                ));
            }
        }
        return new LoadResult(Collections.unmodifiableList(loaded), Collections.unmodifiableList(warnings));
    }
}
