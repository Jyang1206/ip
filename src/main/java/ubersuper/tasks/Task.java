package ubersuper.tasks;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Base class for all task types in UberSuper.
 * <p>
 * Stores the shared state (description, done flag, and {@link TaskType}) and
 * provides common formatting helpers for printing and persistence.
 * Subclasses must provide:
 * <ul>
 *   <li>{@link #formattedString()} — a single-line pipe-separated form for storage</li>
 *   <li>{@link #onDate(LocalDate, int)} — logic to decide if/what to print when filtering by date</li>
 * </ul>
 */
public abstract class Task {
    // ===== Dates & Times =====
    /**
     * ISO local date used for storage (e.g., {@code 2019-12-02}).
     */
    public final DateTimeFormatter STORAGE_DATE = DateTimeFormatter.ISO_LOCAL_DATE;

    /**
     * ISO local date-time used for storage (e.g., {@code 2019-12-02T18:00:00}).
     */
    public final DateTimeFormatter STORAGE_DATETIME = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /**
     * Human-friendly date (e.g., {@code Dec 02 2019}).
     */
    private final DateTimeFormatter DISPLAY_DATE = DateTimeFormatter.ofPattern("MMM dd yyyy");

    /**
     * Human-friendly date-time (e.g., {@code 2019-12-02 18:00}).
     */
    private final DateTimeFormatter DISPLAY_DATETIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private boolean isDone = false;
    private final String description;
    private final TaskType type;

    /**
     * Returns the single-line, pipe-separated representation of this task.
     *
     * @return storage-friendly string
     */
    abstract public String formattedString();

    /**
     * Prints this task when filtering by a given day, if it matches the subclass' criteria.
     * <p>
     * Implementations should:
     * <ol>
     *   <li>Decide if the task "occurs on" {@code day} (e.g., deadlines match exactly; events match if overlapping)</li>
     *   <li>If matched, print a numbered line like {@code "3. [D][ ] description ..."} and return {@code true}</li>
     *   <li>Otherwise return {@code false}</li>
     * </ol>
     *
     * @param day date being queried
     * @param i   1-based index to print if the task matches
     * @return {@code true} if printed; {@code false} otherwise
     */
    abstract public boolean onDate(LocalDate day, int i);

    /**
     * Creates a task with the given description and type.
     *
     * @param description user-visible description of the task
     * @param type        task category tag
     */
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


    public boolean isDone() {
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
        return String.format("[%s][%s] %s", type.getSymbol(), isDone ? "X" : "", description);
    }

    /**
     * Formats a {@link LocalDateTime} for display.
     * <p>
     * If the time-of-day is exactly midnight ({@code 00:00}), only the date is shown
     * using {@link #DISPLAY_DATE}; otherwise date and time are shown using
     * {@link #DISPLAY_DATETIME}.
     *
     * @param dt date-time to format
     * @return human-friendly string for UI output
     */
    public String display(LocalDateTime dt) {
        if (dt.toLocalTime().equals(LocalTime.MIDNIGHT)) {
            return dt.toLocalDate().format(DISPLAY_DATE);
        }
        return dt.format(DISPLAY_DATETIME);
    }
}
