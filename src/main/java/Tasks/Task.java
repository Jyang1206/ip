package Tasks;


import java.time.LocalDateTime;

public abstract class Task {
    private boolean isDone = false;
    private final String description;
    private final TaskType type;

    abstract public String formattedString();

    public Task(String description, TaskType type) {
        this.description = description;
        this.type = type;

    }

    public void mark() {
        this.isDone = true;
    }

    public void unmark() {
        this.isDone = false;
    }

    public boolean done() {
        return isDone;
    }

    public String desc() {
        return description;
    }

    public TaskType type() {
        return type;
    }
    @Override
    public String toString() {
        return String.format("[%s][%s] %s",
                type.getSymbol(),
                isDone ? "X" : "",
                description);
    }
}