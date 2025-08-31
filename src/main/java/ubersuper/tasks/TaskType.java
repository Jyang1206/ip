package ubersuper.tasks;

/**
 * Types of tasks supported by UberSuper.
 * <p>
 * Each enum value has a single-letter symbol used for compact storage and UI printing:
 */
public enum TaskType {
    TODO("T"),
    DEADLINE("D"),
    EVENT("E");

    private final String symbol;

    /**
     * Creates a {@link TaskType} tagged with its single-letter symbol.
     *
     * @param symbol short code used in storage/output (e.g., {@code "T"}, {@code "D"}, {@code "E"})
     */
    TaskType(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

}

