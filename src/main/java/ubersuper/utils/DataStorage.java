package ubersuper.utils;

import ubersuper.tasks.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * Stores {@link Task} data to disk and loads them back at startup.
 *
 * <h2>Storage file</h2>
 * <ul>
 *   <li>Location: {@code data/<fileName>} inside the working directory.</li>
 *   <li>Format: {@code [TaskType] | [Status] | [Description] | [Date/Time] }</li>
 *   <li>TaskType: {@code T} (Todo), {@code D} (Deadline), {@code E} (Event)</li>
 *   <li>Status: {@code 0} for not done, {@code 1} for done</li>
 *   <li>Date/Time:
 *     <ul>
 *       <li>Deadline: one {@code ISO_LOCAL_DATE_TIME} value </li>
 *       <li>Event   : two {@code ISO_LOCAL_DATE_TIME} values (start | end)</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <p>The class also ensures the {@code data/} directory and the target file exist
 * (creating them if missing) before any read/write operation.</p>
 */
@SuppressWarnings("checkstyle:Indentation")
public class DataStorage {
    /**
     * Absolute/relative path to the backing data file (under {@code data/}).
     */
    private final Path dataPath;

    /**
     * Creates a storage that reads/writes to {@code data/<fileName>}.
     *
     * @param fileName file name to use inside the {@code data/} folder (e.g., {@code "uberSuper.txt"})
     */
    @SuppressWarnings({"checkstyle:Indentation", "checkstyle:CommentsIndentation"})
    public DataStorage(String fileName) {
        dataPath = Paths.get("data", fileName);
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
    public LoadedResult load() {
        TaskList tasks = new TaskList(this);
        int skipped = 0;
        try {
            if (Files.notExists(dataPath.getParent())) {
                Files.createDirectories(dataPath.getParent());
            }
            if (Files.notExists(dataPath)) {
                Files.createFile(dataPath);
                return new LoadedResult(tasks, 0, 0);
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

            return new LoadedResult(tasks, tasks.size(), skipped);

        } catch (IOException ioe) {
            return new LoadedResult(tasks, 0, 0);
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
     * @param tasks task list to serialize and persist
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
