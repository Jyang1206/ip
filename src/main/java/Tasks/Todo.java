package Tasks;

import java.time.LocalDate;

public class Todo extends Task {
    public Todo(String description) {
        super(description, TaskType.TODO);
    }

    @Override
    public boolean onDate(LocalDate day, int i) { return false; }

    @Override
    public String formattedString() {
        return String.format("%s | %d | %s",
                type().getSymbol(),
                done() ? 1 : 0,
                desc());
    }
}
