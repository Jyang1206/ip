package ubersuper.utils.storage;

import ubersuper.tasks.*;
import ubersuper.clients.*;
import ubersuper.utils.LoadedResult;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * Stores {@link Task} {@link Client} data to disk and loads them back at startup.
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
public abstract class DataStorage<T> {
    /**
     * Absolute/relative path to the backing data file (under {@code data/}).
     */
    public final Path dataPath;

    /**
     * Creates a storage that reads/writes to {@code data/<fileName>}.
     *
     * @param fileName file name to use inside the {@code data/} folder (e.g., {@code "uberSuper.txt"})
     */
    @SuppressWarnings({"checkstyle:Indentation", "checkstyle:CommentsIndentation"})
    public DataStorage(String fileName) {
        dataPath = Paths.get("data", fileName);
    }

    public Path getDataPath() {
        return dataPath;
    }

    /**
     * Loads tasks from disk into a fresh {@link TaskList} {@link ClientList}.
     * <ul>
     *   <li>Silently skips malformed lines and keeps a count in {@link LoadedResult#skipped()}.</li>
     *   <li>Creates the {@code data/} folder and file if they do not exist.</li>
     *   <li>Parses timestamps using {@link LocalDateTime#parse(CharSequence)} (expects ISO format).</li>
     * </ul>
     *
     * @return a {@link LoadedResult} containing the populated {@link TaskList}, number of tasks loaded,
     * and number of lines skipped.
     **/
    public abstract LoadedResult<T> load();

    /**
     * Saves the current {@link TaskList} {@link ClientList} to disk, overwriting the previous content.
     * <ul>
     *   <li>Ensures the {@code data/} directory exists.</li>
     *   <li>Serializes each task via {@link Task#formatString()}.</li>
     *   <li>Writes using UTF-8; truncates the file first.</li>
     * </ul>
     *
     * @param list a list of objects to be saved in storage
     */
    @SuppressWarnings({"checkstyle:Indentation", "checkstyle:LineLength", "checkstyle:CommentsIndentation"})
    public abstract void save(T list);
}
