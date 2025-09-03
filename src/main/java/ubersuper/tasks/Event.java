package ubersuper.tasks;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * * A {@link Task} that has a start and end {@link LocalDateTime}.
 * <p>
 * The startTime and endTime is stored as a {@link LocalDateTime} and is printed in a
 * user-friendly display format (date-only if time is midnight) by {@link #toString()}
 * while stored in {@link ubersuper.utils.DataStorage} in an ISO-local date-time format
 * by {@link #formattedString()}.,
 */
public class Event extends Task {
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;

    /**
     * Creates an event with the given description and time range.
     *
     * @param description short description of the event
     * @param startTime   start date-time (inclusive)
     * @param endTime     end date-time (inclusive for the date portion)
     */
    public Event(String description, LocalDateTime startTime, LocalDateTime endTime) {
        super(description, TaskType.EVENT);
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * Prints this event if any portion overlaps the given calendar day.
     * <p>
     * A match is computed using date-only comparison:
     * the event matches if {@code day} is in the inclusive range
     * {@code [startTime.toLocalDate(), endTime.toLocalDate()]}.
     * If matched, prints a numbered line and returns {@code true}.
     *
     * @param day date to test for overlap
     * @return {@code true} if printed (overlaps the day); {@code false} otherwise
     */
    @Override
    public boolean isOnDate(LocalDate day) {
        LocalDate s = this.startTime.toLocalDate();
        LocalDate e = this.endTime.toLocalDate();
        return !day.isBefore(s) && !day.isAfter(e);
    }

    @Override
    public String toString() {
        return String.format("[%s][%s] %s %s %s",
                TaskType.EVENT.getSymbol(), isDone() ? "X" : "",
                desc(), "(from: " + display(this.startTime) + ")",
                "(to: " + display(this.endTime)) + ")";
    }

    /**
     * Returns the storage-line form of this event:
     * <pre>
     * E | {0|1} | description | yyyy-MM-dd'T'HH:mm:ss | yyyy-MM-dd'T'HH:mm:ss
     * </pre>
     * where the last field uses {@code STORAGE_DATETIME} (ISO local date-time).
     *
     * @return pipe-separated single-line representation for persistence
     */
    @Override
    public String formattedString() {
        return String.format("%s | %d | %s | %s | %s",
                type().getSymbol(), isDone() ? 1 : 0,
                desc(),
                this.startTime.format((STORAGE_DATETIME)),
                this.endTime.format((STORAGE_DATETIME)));
    }
}
