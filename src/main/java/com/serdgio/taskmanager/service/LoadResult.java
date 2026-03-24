package com.serdgio.taskmanager.service;

import com.serdgio.taskmanager.model.Task;

import java.util.List;

public record LoadResult(List<Task> tasks, List<LoadWarning> warnings) {
}

