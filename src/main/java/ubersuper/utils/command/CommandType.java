package ubersuper.utils.command;

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
    FIND("find"),
    UNKNOWN("");

    private final String keyword;

    CommandType(String keyword) {
        this.keyword = keyword;
    }

    public String getKeyword() {
        return keyword;
    }

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
