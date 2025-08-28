package Utils;

import Tasks.*;

import java.util.ArrayList;

public class LoadedResult {
    private final ArrayList<Task> task;
    private final int taskSize;
    private final int skipped;

    public LoadedResult(ArrayList<Task> task, int taskSize, int skipped) {
        this.task = task;
        this.taskSize = taskSize;
        this.skipped = skipped;
    }
}

