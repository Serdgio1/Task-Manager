# Task Manager (Console)

Simple console-based task manager written in Java.

## Features

- Add, update, delete, and list tasks
- Task fields: title, description, priority, created date, deadline
- Save tasks to file and load tasks from file
- Stable task IDs (IDs are not reindexed after deletion)
- Warning system for legacy/malformed input lines during load

## Project Structure

- `src/main/java/com/serdgio/taskmanager/Main.java` - app entry point
- `src/main/java/com/serdgio/taskmanager/ui/ConsoleUI.java` - console interaction and menu flow
- `src/main/java/com/serdgio/taskmanager/service/TaskService.java` - business logic and validations
- `src/main/java/com/serdgio/taskmanager/service/TaskRepository.java` - repository contract
- `src/main/java/com/serdgio/taskmanager/service/TaskFileRepository.java` - file-based repository implementation
- `src/main/java/com/serdgio/taskmanager/service/TaskSerializer.java` - line serialization/parsing
- `src/main/java/com/serdgio/taskmanager/service/LoadResult.java` - load result model
- `src/main/java/com/serdgio/taskmanager/service/LoadWarning.java` - typed warning model
- `src/main/java/com/serdgio/taskmanager/model/Task.java` - task domain model

## Architecture (Current)

The project uses a small layered design:

- **UI layer** (`ConsoleUI`): reads user input, prints output
- **Service layer** (`TaskService`): business rules and task lifecycle
- **Repository layer** (`TaskRepository`, `TaskFileRepository`): persistence contract + file storage
- **Serialization layer** (`TaskSerializer`): parsing and formatting task lines

This separation makes it easier to add a new storage backend (for example JSON or DB) without rewriting service logic.

## Running

There is no Maven/Gradle config in the repository yet, so run from IDE or with `javac/java`.

### Option 1: Run from IDE

Run `Main.main()` from:

- `src/main/java/com/serdgio/taskmanager/Main.java`

### Option 2: Run from terminal

From repository root:

```bash
mkdir -p out
javac -d out $(find src/main/java -name "*.java")
java -cp out com.serdgio.taskmanager.Main
```

## Data Format

Primary format is tab-delimited line per task:

`id <tab> title <tab> description <tab> createdAt(ISO-8601) <tab> deadline(dd.MM.yyyy or empty) <tab> priority`

Example:

```text
1	task one	desc	2026-03-01T00:00:00Z	10.03.2026	HIGH
2	task two		2026-03-02T00:00:00Z		LOW
```

Escaping:

- `\t` inside fields is escaped
- `\` is escaped

Legacy lines are still supported in strict mode, but warnings are returned via `LoadWarning`.

## Testing

Current tests are split by layer:

- `TaskServiceTest` - business logic
- `TaskSerializerTest` - parsing/serialization behavior
- `TaskFileRepositoryTest` - file load/save behavior and warning codes

