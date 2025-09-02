package ubersuper.tasks;

import java.time.LocalDate;

/**
 * A simple task with only a description and a done flag (no date attached).
 * <p>
 * {@code Todo} are considered non-date tasks.
 * {@link TaskList#onDate(String)} always returns {@code false}
 */
public class Todo extends Task {
    public Todo(String description) {
        super(description, TaskType.TODO);
    }

    /**
     * {@inheritDoc}
     * <p>
     * {@code Todo} has no associated date, so it never matches and nothing is printed.
     *
     * @return always {@code false}
     */
    @Override
    public boolean onDate(LocalDate day, int i) {
        return false;
    }

    /**
     * Returns the storage-line form of this task:
     * <pre>
     * T | {0|1} | description
     * </pre>
     *
     * @return pipe-separated single-line representation for persistence
     */
    @Override
    public String formattedString() {
        return String.format("%s | %d | %s",
                type().getSymbol(),
                isDone() ? 1 : 0,
                desc());
    }
}
