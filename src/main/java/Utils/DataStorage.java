package Utils;

import Tasks.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

// handles saving and loading logic
// Format: [TaskType] | [Status] | Description | Date/Time
public class DataStorage {

        private final Path DATA_PATH;

        public DataStorage(String fileName) {
            DATA_PATH = Paths.get("data", fileName);
        }

        public LoadedResult load() {
            TaskList tasks = new TaskList(this);
            int skipped = 0;
            try {
                if (Files.notExists(DATA_PATH.getParent())) {
                    Files.createDirectories(DATA_PATH.getParent());
                }
                if (Files.notExists(DATA_PATH)) {
                    Files.createFile(DATA_PATH);
                    return new LoadedResult(tasks, 0, 0);
                }
                List<String> lines = Files.readAllLines(DATA_PATH, StandardCharsets.UTF_8);
                for (String data : lines) {
                    String line = data.trim();
                    if (line.isEmpty()) continue;
                    String[] parts = line.split("\\|");
                    for (int i = 0; i < parts.length; i++) {
                        parts[i] = parts[i].trim();
                    }

                    try {
                        // incomplete prompt
                        if (parts.length < 3) {
                            skipped++;
                            continue;
                        }
                        String type = parts[0];
                        int done = Integer.parseInt(parts[1]);
                        String description = parts[2];
                        switch (type) {
                            case "T": {
                                Todo t = new Todo(description);
                                if (done == 1) {
                                    t.mark();
                                }
                                tasks.add(t);
                                break;
                            }
                            case "D":{
                                // incomplete prompt
                                if (parts.length < 4) {
                                    skipped++;
                                    break;
                                }
                                try {
                                    LocalDateTime deadline = LocalDateTime.parse(parts[3]);
                                    Deadline d = new Deadline(description, deadline);
                                    if (done == 1) {
                                        d.mark();
                                    }
                                    tasks.add(d);
                                } catch (DateTimeParseException e) {
                                    skipped++;
                                }
                                break;
                            }

                            case "E": {
                                if (parts.length < 5) { skipped++; break; }
                                try {
                                    LocalDateTime start = LocalDateTime.parse(parts[3]);
                                    LocalDateTime end   = LocalDateTime.parse(parts[4]);
                                    Event e = new Event(description, start, end);
                                    if (done == 1) e.mark();
                                    tasks.add(e);
                                } catch (DateTimeParseException ex) { skipped++; }
                                break;
                            }

                            default:
                                // task not labelled correctly
                                skipped++;
                        }
                    } catch (Exception e) {
                        skipped++;
                    }
                }
                return new LoadedResult(tasks, tasks.size(), skipped);
            } catch (IOException ioe) {
                return new LoadedResult(tasks, 0, 0);
            }
        }

        public void save(TaskList tasks) {
            try {
                if (Files.notExists(DATA_PATH.getParent())) {
                    Files.createDirectories(DATA_PATH.getParent());
                }
                List<String> lines = tasks.stream()
                        .map(Task::formattedString)
                        .collect(Collectors.toList());
                Files.write(DATA_PATH, lines, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            } catch (IOException ioe) {
                System.out.print("Could not save tasks!");
            }
        }
    }
