package ubersuper.utils.command;


/**
 * Top-level commands supported by the UberSuper CLI.
 * <p>
 * Each enum constant carries its lower-case keyword (e.g., {@code "list"}, {@code "ondate"}).
 */
public enum CommandType {
    BYE("bye"),
    TASKLIST("listtask"),
    CLIENTLIST("listclient"),
    MARK("mark"),
    UNMARK("unmark"),
    TODO("todo"),
    DEADLINE("deadline"),
    EVENT("event"),
    DELETETASK("deletetask"),
    DELETECLIENT("deleteclient"),
    ONDATE("ondate"),
    FINDTASK("findtask"),
    FINDCLIENT("findclient"),
    ADDCLIENT("addclient"),
    UNKNOWN("");

    private final String keyword;


    CommandType(String keyword) {
        this.keyword = keyword;
    }

    public String getKeyword() {
        return keyword;
    }
}

