package ubersuper.tasks;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Deadline extends Task {
    private final LocalDateTime deadLine;

    public Deadline(String description, LocalDateTime deadLine) {
        super(description, TaskType.DEADLINE);
        this.deadLine = deadLine;
    }

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
                done() ? "X" : "",
                desc(),
                "(by: " + display(this.deadLine) + ")");
    }
    @Override
    public String formattedString() {
        return String.format("%s | %d | %s | %s",
                type().getSymbol(),
                done() ? 1 : 0,
                desc(),
                this.deadLine.format(STORAGE_DATETIME));
    }
}
