package Utils;

import Tasks.*;

import java.util.ArrayList;

public class LoadedResult {
    private final TaskList task;
    private final int taskSize;
    private final int skipped;

    public LoadedResult(TaskList task, int taskSize, int skipped) {
        this.task = task;
        this.taskSize = taskSize;
        this.skipped = skipped;
    }

    public int taskSize() {
        return this.taskSize;
    }

    public int skipped() {
        return this.skipped;
    }

    public TaskList task() {
        return this.task;
    }

}

