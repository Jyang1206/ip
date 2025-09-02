package ubersuper.utils.command;


/**
 * Top-level commands supported by the UberSuper CLI.
 * <p>
 * Each enum constant carries its lower-case keyword (e.g., {@code "list"}, {@code "ondate"}).
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
}
