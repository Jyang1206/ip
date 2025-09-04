package ubersuper.utils.command;


import ubersuper.exceptions.UberExceptions;
import ubersuper.tasks.TaskList;
import ubersuper.utils.Parser;
import ubersuper.utils.ui.Ui;

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
    FIND("find"),
    UNKNOWN("");

    private final String keyword;


    CommandType(String keyword) {
        this.keyword = keyword;
    }

    public String getKeyword() {
        return keyword;
    }
}

