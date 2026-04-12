package com.serdgio.taskmanager.service;

import com.serdgio.taskmanager.model.Task;

import java.util.List;

/**
 * Result of repository load operation.
 *
 * @param tasks successfully parsed tasks
 * @param warnings non-fatal warnings produced during parsing
 */
public record LoadResult(List<Task> tasks, List<LoadWarning> warnings) {}
