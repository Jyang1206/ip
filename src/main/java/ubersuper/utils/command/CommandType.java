package ubersuper.utils.command;


/**
 * Top-level commands supported by the UberSuper CLI.
 * <p>
 * Each enum constant carries its lower-case keyword (e.g., {@code "list"}, {@code "ondate"}).
 * Parsing user input is done via {@link #fromInput(String)}, which:
 * <ul>
 *   <li>is case-insensitive (e.g., {@code "Bye"}, {@code "BYE"} → {@link #BYE}),</li>
 *   <li>matches the <em>first whitespace-delimited token</em> only,</li>
 *   <li>requires an exact token match (no prefix matching; e.g., {@code "listall"} → {@link #UNKNOWN}),</li>
 *   <li>is null/blank-safe (null or blank input → {@link #UNKNOWN}).</li>
 * </ul>
 */
public enum CommandType {
    BYE("bye"),
    LIST("list"),
    MARK("mark"),
    UNMARK("unmark"),
    TODO("todo"),
    DEADLINE("deadline"),
    EVENT("event"),
    DELETE("delete"),
    ONDATE("ondate"),
    UNKNOWN("");

    private final String keyword;

    CommandType(String keyword) {
        this.keyword = keyword;
    }

    public String getKeyword() {
        return keyword;
    }
    /**
 * Parses the user's input into a {@link CommandType}.
 * <p><strong>Rules:</strong></p>
 * <ul>
 *   <li>Extracts the first whitespace-delimited token and lower-cases it.</li>
 *   <li>Performs exact token match against known commands (no prefix/substring match).</li>
 *   <li>Returns {@link #UNKNOWN} if the token does not match any command, or if input is null/blank.</li>
 * </ul>
 *
 * @param input full user input line
 * @return matching {@link CommandType} or {@link #UNKNOWN} if none
 */
    public static CommandType fromInput(String input) {

        if (input == null || input.isBlank()) {
            return UNKNOWN;
        }

        String head = input.strip().split("\\s+", 2)[0].toLowerCase();

        for (CommandType c : values()) {
            if (head.equals(c.keyword)) {
                return c;
            }
        }
        return UNKNOWN;
    }
}
