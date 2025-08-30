package Utils;

public enum CommandType {
    BYE("bye"),
    LIST("list"),
    MARK("mark"),
    UNMARK("unmark"),
    TODO("todo"),
    DEADLINE("deadline"),
    EVENT("event"),
    DELETE("delete"),
    ONDATE("onDate"),
    UNKNOWN("");

    private final String keyword;

    CommandType(String keyword) {
        this.keyword = keyword;
    }
    public String getKeyword() {
        return keyword;
    }
    public static CommandType fromInput(String input) {
        for (CommandType c : values()) {
            if (input.startsWith(c.keyword)) {
                return c;
            }
        }
        return UNKNOWN;
    }
}
