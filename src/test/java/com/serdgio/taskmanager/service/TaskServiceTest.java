package com.serdgio.taskmanager.service;

import com.serdgio.taskmanager.model.Task;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskServiceTest {
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        taskService = new TaskService();
    }

    @Test
    void deleteTask_WhenIdDoesNotExist_doesNotChangeList() {
        taskService.addTask("t1", "d1", LocalDate.of(2026, 3, 5));
        taskService.addTask("t2", "d2", LocalDate.of(2026,3,6));

        int before = taskService.getTasks().size();
        taskService.deleteTask(999);
        int after = taskService.getTasks().size();

        assertEquals(before, after);
    }

    @Test
    void deleteTask_WhenRemoved_reindexesIdsFrom1ToN() {
        taskService.addTask("t1", "d1", LocalDate.of(2026, 3, 5));
        taskService.addTask("t2", "d2", LocalDate.of(2026,3,6));
        taskService.addTask("t3", "d3", LocalDate.of(2026,3,7));

        taskService.deleteTask(2);

        List<Task> tasks = taskService.getTasks();
        assertEquals(2, tasks.size());
        assertEquals(1, tasks.get(0).getId());
        assertEquals(2, tasks.get(1).getId());
    }
}