package ubersuper.utils.storage;

import ubersuper.tasks.Deadline;
import ubersuper.tasks.Event;
import ubersuper.tasks.Task;
import ubersuper.tasks.TaskList;
import ubersuper.tasks.Todo;
import ubersuper.utils.LoadedResult;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TaskStorage extends DataStorage<TaskList> {

    public TaskStorage() {
        super("uberSuperTasks.txt");
    }

    /**
     * Loads tasks from disk into a fresh {@link TaskList}.
     * <ul>
     *   <li>Silently skips malformed lines and keeps a count in {@link LoadedResult#skipped()}.</li>
     *   <li>Creates the {@code data/} folder and file if they do not exist.</li>
     *   <li>Parses timestamps using {@link LocalDateTime#parse(CharSequence)} (expects ISO format).</li>
     * </ul>
     *
     * @return a {@link LoadedResult} containing the populated {@link TaskList}, number of tasks loaded,
     * and number of lines skipped.
     **/
    public LoadedResult<TaskList> load() {
        TaskList tasks = new TaskList(this);
        int skipped = 0;
        try {
            if (Files.notExists(dataPath.getParent())) {
                Files.createDirectories(dataPath.getParent());
            }
            if (Files.notExists(dataPath)) {
                Files.createFile(dataPath);
                return new LoadedResult<TaskList>(tasks, 0, 0);
            }
            List<String> lines = Files.readAllLines(dataPath, StandardCharsets.UTF_8);
            // parse each line -> Task or null
            List<Task> parsedTasks = lines.stream()
                    .map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .map(line -> {
                        try {
                            String[] parts = line.split("\\|");
                            for (int i = 0; i < parts.length; i++) {
                                parts[i] = parts[i].trim();
                            }

                            if (parts.length < 3) {
                                return null;
                            }

                            String type = parts[0];
                            int done = Integer.parseInt(parts[1]);
                            String description = parts[2];

                            switch (type) {
                            case "T" -> {
                                Todo t = new Todo(description);
                                if (done == 1) {
                                    t.mark();
                                }
                                return t;
                            }
                            case "D" -> {
                                if (parts.length < 4) {
                                    return null;
                                }
                                LocalDateTime deadline = LocalDateTime.parse(parts[3]);
                                Deadline d = new Deadline(description, deadline);
                                if (done == 1) {
                                    d.mark();
                                }
                                return d;
                            }
                            case "E" -> {
                                if (parts.length < 5) {
                                    return null;
                                }
                                LocalDateTime start = LocalDateTime.parse(parts[3]);
                                LocalDateTime end = LocalDateTime.parse(parts[4]);
                                Event e = new Event(description, start, end);
                                if (done == 1) {
                                    e.mark();
                                }
                                return e;
                            }
                            default -> {
                                return null;
                            }
                            }
                        } catch (Exception e) {
                            return null;
                        }
                    })
                    .toList();

            // add valid tasks to TaskList
            parsedTasks.stream()
                    .filter(Objects::nonNull)
                    .forEach(tasks::add);

            // count skipped lines
            skipped = (int) parsedTasks.stream().filter(Objects::isNull).count();

            return new LoadedResult<TaskList>(tasks, tasks.size(), skipped);

        } catch (IOException ioe) {
            return new LoadedResult<TaskList>(tasks, 0, 0);
        }
    }

    /**
     * Saves the current {@link TaskList} to disk, overwriting the previous content.
     * <ul>
     *   <li>Ensures the {@code data/} directory exists.</li>
     *   <li>Serializes each task via {@link Task#formatString()}.</li>
     *   <li>Writes using UTF-8; truncates the file first.</li>
     * </ul>
     *
     * @param tasks list of tasks to be saved
     */
    @SuppressWarnings({"checkstyle:Indentation", "checkstyle:LineLength", "checkstyle:CommentsIndentation"})
    public void save(TaskList tasks) {
        try {
            if (Files.notExists(dataPath.getParent())) {
                Files.createDirectories(dataPath.getParent());
            }
            List<String> lines = tasks.stream().map(Task::formatString).collect(Collectors.toList());
            Files.write(dataPath,
                    lines,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException ioe) {
            System.out.print("Could not save tasks!");
        }
    }
}
