package Tasks;

import java.time.LocalDateTime;

public class Event extends Task {
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;

    public Event(String description, LocalDateTime startTime, LocalDateTime endTime) {
        super(description, TaskType.EVENT);
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return String.format("[%s][%s] %s %s %s",
                TaskType.EVENT.getSymbol(),
                done() ? "X" : "",
                desc(),
                "(from: " + display(this.startTime) + ")",
                "(to: " + display(this.endTime)) + ")";
    }
    @Override
    public String formattedString() {
        return String.format("%s | %d | %s | %s | %s",
                type().getSymbol(),
                done() ? 1 : 0,
                desc(),
                this.startTime.format((STORAGE_DATETIME)),
                this.endTime.format((STORAGE_DATETIME)));
    }