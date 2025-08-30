package Utils;

import Tasks.*;

import java.util.ArrayList;

public class LoadedResult {
    private final TaskList tasks;
    private final int taskSize;
    private final int skipped;

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

