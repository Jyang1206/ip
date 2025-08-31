package ubersuper.utils;

import ubersuper.tasks.TaskList;

/**
 * Result of loading tasks from persistent storage.
 * <p>
 * This is a simple immutable holder returned by {@link DataStorage#load()} that
 * carries:
 * <ul>
 *   <li>{@code tasks}: the loaded {@link TaskList} (possibly empty),</li>
 *   <li>{@code taskSize}: number of tasks successfully loaded,</li>
 *   <li>{@code skipped}: number of lines skipped due to parse/format errors.</li>
 * </ul>
 */
public class LoadedResult {
    private final TaskList tasks;
    private final int taskSize;
    private final int skipped;

    /**
     * Creates a {@code LoadedResult}.
     *
     * @param tasks    the loaded task list (non-null)
     * @param taskSize count of tasks successfully loaded into {@code tasks}
     * @param skipped  count of corrupted/unsupported lines skipped during load
     */
    public LoadedResult(TaskList tasks, int taskSize, int skipped) {
        this.tasks = tasks;
        this.taskSize = taskSize;
        this.skipped = skipped;
    }

    public int taskSize() {
        return this.taskSize;
    }

    public int skipped() {
        return this.skipped;
    }

    public TaskList tasks() {
        return this.tasks;
    }

}

