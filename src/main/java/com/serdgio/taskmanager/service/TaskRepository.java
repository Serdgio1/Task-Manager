package com.serdgio.taskmanager.service;

import com.serdgio.taskmanager.model.Task;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface TaskRepository {
    void save(Path path, List<Task> tasks) throws IOException;

    LoadResult load(Path path) throws IOException;
}

