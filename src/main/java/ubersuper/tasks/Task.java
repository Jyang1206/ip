package ubersuper.tasks;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public abstract class Task {
    private boolean isDone = false;
    private final String description;
    private final TaskType type;

    // ===== Dates & Times =====
    private final DateTimeFormatter DISPLAY_DATE = DateTimeFormatter.ofPattern("MMM dd yyyy");
    private final DateTimeFormatter DISPLAY_DATETIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    public final DateTimeFormatter STORAGE_DATE = DateTimeFormatter.ISO_LOCAL_DATE;
    public final DateTimeFormatter STORAGE_DATETIME = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    abstract public String formattedString();
    abstract public boolean onDate(LocalDate day , int i);

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
    // Convenient display: if time is 00:00, show date only
    public String display(LocalDateTime dt) {
        if (dt.toLocalTime().equals(LocalTime.MIDNIGHT)) {
            return dt.toLocalDate().format(DISPLAY_DATE);
        }
        return dt.format(DISPLAY_DATETIME);
    }
}