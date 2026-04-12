package com.serdgio.taskmanager.service;

import com.serdgio.taskmanager.model.Task;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Persistence contract for storing and loading task collections.
 */
public interface TaskRepository {
    /**
     * Saves all tasks to the provided path.
     *
     * @param path target file path
     * @param tasks tasks to persist
     * @throws IOException when writing fails
     */
    void save(Path path, List<Task> tasks) throws IOException;

    /**
     * Loads tasks from the provided path.
     *
     * @param path source file path
     * @return immutable load result with parsed tasks and structured warnings
     * @throws IOException when reading fails
     */
    LoadResult load(Path path) throws IOException;
}
