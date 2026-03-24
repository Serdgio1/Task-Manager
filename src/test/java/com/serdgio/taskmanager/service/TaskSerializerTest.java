package com.serdgio.taskmanager.service;

import com.serdgio.taskmanager.model.Priority;
import com.serdgio.taskmanager.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TaskSerializerTest {
    private TaskSerializer serializer;

    @BeforeEach
    void setUp() {
        serializer = new TaskSerializer();
    }

    @Test
    void toLine_parseDelimited_RoundTripPreservesFields() {
        Task task = new Task(1, "task one", "desc with spaces",
                Instant.parse("2026-03-01T00:00:00Z"),
                LocalDate.of(2026, 3, 10).atStartOfDay(),
                Priority.HIGH);

        String line = serializer.toLine(task);
        Task parsed = serializer.parseDelimited(line);

        assertNotNull(parsed);
        assertEquals(1, parsed.getId());
        assertEquals("task one", parsed.getTitle());
        assertEquals("desc with spaces", parsed.getDescription());
        assertEquals(Priority.HIGH, parsed.getPriority());
        assertEquals(LocalDate.of(2026, 3, 10), parsed.getDeadline().toLocalDate());
    }

    @Test
    void toLine_parseDelimited_WithTabAndBackslash_PreservesValues() {
        Task task = new Task(7, "task\\name", "first\tsecond\\third",
                Instant.parse("2026-03-01T00:00:00Z"),
                null,
                Priority.MEDIUM);

        String line = serializer.toLine(task);
        Task parsed = serializer.parseDelimited(line);

        assertNotNull(parsed);
        assertEquals("task\\name", parsed.getTitle());
        assertEquals("first\tsecond\\third", parsed.getDescription());
    }

    @Test
    void parseLegacy_WhenLineHasUnsupportedStructure_ReturnsNull() {
        Task parsed = serializer.parseLegacy("1 title with spaces 2026-03-01T00:00:00Z 10.03.2026 HIGH");
        assertNull(parsed);
    }
}

