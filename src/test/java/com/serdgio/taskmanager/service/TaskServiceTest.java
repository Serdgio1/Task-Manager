package com.serdgio.taskmanager.service;

import com.serdgio.taskmanager.model.Priority;
import com.serdgio.taskmanager.model.Task;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskServiceTest {
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        taskService = new TaskService();
    }

    @Test
    void deleteTask_WhenIdDoesNotExist_doesNotChangeList() {
        taskService.addTask("t1", "d1", Priority.MEDIUM, LocalDate.of(2026, 3, 5).atStartOfDay());
        taskService.addTask("t2", "d2", Priority.MEDIUM, LocalDate.of(2026,3,6).atStartOfDay());

        int before = taskService.getTasks().size();
        boolean deleted = taskService.deleteTask(999);
        int after = taskService.getTasks().size();

        assertFalse(deleted);
        assertEquals(before, after);
    }

    @Test
    void deleteTask_WhenRemoved_keepsRemainingIdsStable() {
        taskService.addTask("t1", "d1", Priority.MEDIUM, LocalDate.of(2026, 3, 5).atStartOfDay());
        taskService.addTask("t2", "d2", Priority.MEDIUM, LocalDate.of(2026,3,6).atStartOfDay());
        taskService.addTask("t3", "d3", Priority.MEDIUM, LocalDate.of(2026,3,7).atStartOfDay());

        boolean deleted = taskService.deleteTask(2);

        List<Task> tasks = taskService.getTasks();
        assertTrue(deleted);
        assertEquals(2, tasks.size());
        assertEquals(1, tasks.get(0).getId());
        assertEquals(3, tasks.get(1).getId());
    }

    @Test
    void updateTask_WhenIdDoesNotExist_doesNotChangeList() {
        taskService.addTask("t1", "d1", Priority.MEDIUM, LocalDate.of(2026, 3, 5).atStartOfDay());
        taskService.addTask("t2", "d2", Priority.MEDIUM, LocalDate.of(2026,3,6).atStartOfDay());

        boolean updated = taskService.updateTask(999, "new", "test", Priority.LOW, LocalDate.of(2026, 3, 5).atStartOfDay());

        List<Task> tasks = taskService.getTasks();
        assertFalse(updated);
        assertEquals(2, tasks.size());
        assertEquals(1, tasks.getFirst().getId());
        assertEquals("t1", tasks.getFirst().getTitle());
        assertEquals("d1", tasks.getFirst().getDescription());

    }

    @Test
    void addTask_WhenNameIsBlank_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> taskService.addTask("", "d1", Priority.MEDIUM, LocalDate.of(2026, 3, 5).atStartOfDay()));
    }

    @Test
    void updateTask_WhenNameIsBlank_throwsIllegalArgumentException() {
        taskService.addTask("t1", "d1", Priority.MEDIUM, LocalDate.of(2026, 3, 5).atStartOfDay());
        assertThrows(IllegalArgumentException.class,
                () -> taskService.updateTask(1, "", "d2", Priority.LOW, LocalDate.of(2026, 3, 5).atStartOfDay()));
    }

    @Test
    void updateTask_WhenIdExists_UpdatesNormalizedFields() {
        taskService.addTask("t1", "d1", Priority.MEDIUM, LocalDate.of(2026, 3, 5).atStartOfDay());
        taskService.updateTask(1, "new", "test", Priority.LOW, LocalDate.of(2026, 3, 5).atStartOfDay());
        List<Task> tasks = taskService.getTasks();
        assertEquals(1, tasks.size());
        assertEquals("new", tasks.getFirst().getTitle());
        assertEquals("test", tasks.getFirst().getDescription());
        assertEquals(Priority.LOW, tasks.getFirst().getPriority());
        assertEquals(LocalDate.of(2026, 3, 5), tasks.getFirst().getDeadline().toLocalDate());
    }

    @Test
    void findById_WhenIdExists_ReturnsTask() {
        taskService.addTask("t1", "d1", Priority.MEDIUM, LocalDate.of(2026, 3, 5).atStartOfDay());
        taskService.addTask("t2", "d2", Priority.MEDIUM, LocalDate.of(2026, 3, 6).atStartOfDay());

        assertTrue(taskService.findById(2).isPresent());
        assertEquals("t2", taskService.findById(2).orElseThrow().getTitle());
    }

    @Test
    void getTasks_WhenAttemptToModify_ThrowsUnsupportedOperationException() {
        taskService.addTask("t1", "d1", Priority.MEDIUM, LocalDate.of(2026, 3, 5).atStartOfDay());

        List<Task> tasks = taskService.getTasks();
        assertThrows(UnsupportedOperationException.class, tasks::clear);
    }

    @Test
    void replaceAllTasks_AfterLoad_AddTaskUsesMaxIdPlusOne() {
        Task t1 = new Task(5, "task one", "d1", java.time.Instant.parse("2026-03-01T00:00:00Z"),
                LocalDate.of(2026, 3, 10).atStartOfDay(), Priority.HIGH);
        Task t2 = new Task(9, "task two", null, java.time.Instant.parse("2026-03-02T00:00:00Z"),
                null, Priority.LOW);

        taskService.replaceAllTasks(List.of(t1, t2));
        taskService.addTask("after load", "new", Priority.MEDIUM, LocalDate.of(2026, 3, 20).atStartOfDay());

        List<Task> tasks = taskService.getTasks();
        assertEquals(3, tasks.size());
        assertEquals(5, tasks.get(0).getId());
        assertEquals(9, tasks.get(1).getId());
        assertEquals(10, tasks.get(2).getId());
    }

    @Test
    void replaceAllTasks_WhenNullList_ThrowsIllegalArgumentException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> taskService.replaceAllTasks(null));
        assertTrue(ex.getMessage().contains("cannot be null"));
    }

    @Test
    void replaceAllTasks_WhenContainsNullTask_ThrowsIllegalArgumentException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> taskService.replaceAllTasks(java.util.Arrays.asList(
                        new Task(1, "ok", "d1", java.time.Instant.parse("2026-03-01T00:00:00Z"),
                                null, Priority.LOW),
                        null)));
        assertTrue(ex.getMessage().contains("null elements"));
    }

    @Test
    void replaceAllTasks_WhenContainsDuplicateIds_ThrowsIllegalArgumentException() {
        Task t1 = new Task(7, "first", "d1", java.time.Instant.parse("2026-03-01T00:00:00Z"),
                null, Priority.MEDIUM);
        Task t2 = new Task(7, "second", "d2", java.time.Instant.parse("2026-03-02T00:00:00Z"),
                null, Priority.HIGH);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> taskService.replaceAllTasks(List.of(t1, t2)));
        assertTrue(ex.getMessage().contains("duplicate id"));
    }
}