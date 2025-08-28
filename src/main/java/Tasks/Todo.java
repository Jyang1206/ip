package Tasks;

public class Todo extends Task {
    public Todo(String description) {
        super(description, TaskType.TODO);
    }

    @Override
    public String formattedString() {
        return String.format("%s | %d | %s",
                type().getSymbol(),
                done() ? 1 : 0,
                desc());
    }
}
