package ubersuper.tasks;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * A {@link Task} that has a deadline timestamp.
 * <p>
 * The deadline is stored as a {@link LocalDateTime} and is printed in a
 * user-friendly format by {@link #toString()} while stored in {@link ubersuper.utils.DataStorage} in an
 * ISO-local date-time format by {@link #formattedString()}.
 */
public class Deadline extends Task {
    private final LocalDateTime deadLine;

    /**
     * Creates a deadline task.
     *
     * @param description short description of the task
     * @param deadLine    the moment this task is due (not {@code null})
     */
    public Deadline(String description, LocalDateTime deadLine) {
        super(description, TaskType.DEADLINE);
        this.deadLine = deadLine;
    }

    /**
     * Prints this task if its deadline falls on the given calendar day.
     * <p>
     * A match is <em>date-only</em> (time-of-day is ignored). If matched, this method prints
     * a numbered entry (e.g., {@code "3. [D][ ] ..."}) and returns {@code true}.
     *
     * @param day date to compare with the deadline's date portion
     * @param i   1-based index to print alongside the task if it matches
     * @return {@code true} if the deadline is on {@code day} and was printed; {@code false} otherwise
     */
    @Override
    public boolean onDate(LocalDate day, int i) {
        LocalDate d = this.deadLine.toLocalDate();
        if (d.equals(day)) {
            System.out.println(i++ + ". " + this);
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("[%s][%s] %s %s",
                TaskType.DEADLINE.getSymbol(),
                isDone() ? "X" : "",
                desc(),
                "(by: " + display(this.deadLine) + ")");
    }

    /**
     * Returns the storage-line form of this task:
     * <pre>
     * D | {0|1} | description | yyyy-MM-dd'T'HH:mm:ss
     * </pre>
     * where the last field uses {@code STORAGE_DATETIME} (ISO local date-time).
     *
     * @return pipe-separated single-line representation
     */
    @Override
    public String formattedString() {
        return String.format("%s | %d | %s | %s",
                type().getSymbol(),
                isDone() ? 1 : 0,
                desc(),
                this.deadLine.format(STORAGE_DATETIME));
    }
}
