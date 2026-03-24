package com.serdgio.taskmanager.service;

import com.serdgio.taskmanager.model.Priority;
import com.serdgio.taskmanager.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskFileRepositoryTest {
    private TaskFileRepository repository;

    @BeforeEach
    void setUp() {
        repository = new TaskFileRepository(new TaskSerializer());
    }

    @Test
    void saveLoad_RoundTrip_PreservesIdsAndFields(@TempDir Path tempDir) throws Exception {
        List<Task> tasks = List.of(
                new Task(1, "task one", "desc with spaces", Instant.parse("2026-03-01T00:00:00Z"),
                        LocalDate.of(2026, 3, 10).atStartOfDay(), Priority.HIGH),
                new Task(2, "task two", null, Instant.parse("2026-03-02T00:00:00Z"),
                        null, Priority.LOW)
        );

        Path file = tempDir.resolve("tasks.txt");
        repository.save(file, tasks);

        LoadResult result = repository.load(file);

        assertEquals(2, result.tasks().size());
        assertTrue(result.warnings().isEmpty());
        assertEquals(1, result.tasks().get(0).getId());
        assertEquals("task one", result.tasks().get(0).getTitle());
        assertEquals("desc with spaces", result.tasks().get(0).getDescription());
        assertEquals(2, result.tasks().get(1).getId());
        assertNull(result.tasks().get(1).getDescription());
    }

    @Test
    void load_WhenLegacyLineUsed_ReturnsWarning(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve("legacy.txt");
        Files.writeString(file, "1 title desc 2026-03-01T00:00:00Z 10.03.2026 HIGH\n");

        LoadResult result = repository.load(file);

        assertEquals(1, result.tasks().size());
        assertTrue(result.warnings().stream().anyMatch(w -> w.code() == LoadWarning.Code.LEGACY_FORMAT));
    }

    @Test
    void load_WhenLegacyLineUnsupported_ReturnsSkipWarning(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve("legacy-unsupported.txt");
        Files.writeString(file, "1 title with spaces 2026-03-01T00:00:00Z 10.03.2026 HIGH\n");

        LoadResult result = repository.load(file);

        assertEquals(0, result.tasks().size());
        assertTrue(result.warnings().stream().anyMatch(w -> w.code() == LoadWarning.Code.LEGACY_FORMAT));
        assertTrue(result.warnings().stream().anyMatch(w -> w.code() == LoadWarning.Code.UNSUPPORTED_LEGACY_LINE));
    }

    @Test
    void load_WhenMalformedDelimitedLine_ReturnsMalformedWarning(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve("malformed.txt");
        Files.writeString(file, "1\ttitle\tdesc\tbad-instant\t10.03.2026\tHIGH\n");

        LoadResult result = repository.load(file);

        assertEquals(0, result.tasks().size());
        assertTrue(result.warnings().stream().anyMatch(w -> w.code() == LoadWarning.Code.MALFORMED_LINE));
    }
}

