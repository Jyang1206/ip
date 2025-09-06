package ubersuper.utils.ui;

import ubersuper.exceptions.UberExceptions;
import ubersuper.tasks.TaskList;
import ubersuper.utils.LoadedResult;
import ubersuper.utils.Parser;
import ubersuper.utils.command.CommandType;



/**
 * Handles all user-facing I/O for the UberSuper app.
 * <p>
 * Responsibilities:
 * <ul>
 *   <li>Routing recognized commands to the appropriate {@link TaskList} methods</li>
 *   <li>Printing standard UI messages (greeting, divider line, goodbye, errors)</li>
 * </ul>
 * <p>
 * This class does not mutate storage directly; all task operations are delegated
 * to {@link TaskList}. The command parsing of the first token is handled by
 * {@link Parser#fromInput(String)}.
 */
public class Ui {

    private static final String BOT_NAME = "UberSuper";
    private static final String LINE = "_________________________________";
    private final TaskList tasks;

    /**
     * @param tasks the task list to operate on when handling commands
     */
    public Ui(TaskList tasks) {
        assert tasks != null : "Ui must be created with a non-null TaskList";
        this.tasks = tasks;
    }
    /**
     * Runs the main command loop.
     * <p>
     * using {@link Parser#fromInput(String)}, and invokes the corresponding
     * operation on {@link TaskList}. The loop terminates when the user enters
     * the {@code bye} command, after printing a farewell message.
     * <p>
     * If a command is unknown or a handler throws an {@link UberExceptions},
     * an error message is printed and the loop continues to read the next line.
     */
    public String echo(String raw) {
        String input = raw.trim();
        CommandType command = Parser.fromInput(input);
        assert command != null : "Parser must return a valid CommandType";

        try {
            switch (command) {
            case BYE:
                return goodBye();
            case LIST:
                return tasks.list();
            case MARK:
                return tasks.mark(input);
            case UNMARK:
                return tasks.unmark(input);
            case TODO:
                return tasks.todo(input);
            case DEADLINE:
                return tasks.deadline(input);
            case EVENT:
                return tasks.event(input);
            case DELETE:
                return tasks.delete(input);
            case ONDATE:
                return tasks.onDate(input);
            case FIND:
                return tasks.find(input);
            case UNKNOWN:
            default:
                throw new UberExceptions("Sorry! I have no idea what you're trying to do.");
            }
        } catch (UberExceptions e) {
            return Ui.printLine() + e.getMessage() + "\n" + Ui.printLine();
        }
    }
    /**
     * Prints a standard horizontal divider line used by the UI.
     * <p>
     * This is a convenience utility so other classes can use the same divider.
     */

    public static String printLine() {
        return LINE + "\n";
    }

    /**
     * Prints the farewell message and a divider line.
     * <p>
     * Typically invoked when the {@code bye} command is received.
     *
     * @return String message
     */
    public String goodBye() {
        return "Bye. Hope to see you again soon! \n" + printLine();
    }


    /**
     * Prints the initial greeting and, if applicable, a summary of the load results.
     * <p>
     * When prior tasks are found on disk, shows how many were loaded and how many
     * lines were skipped due to errors, then prints the current list of tasks.
     * Otherwise, informs the user that the list is empty.
     *
     * @param result the outcome of loading tasks from disk
     */
    public String greet(LoadedResult result) {
        String message = "";
        // show result if available, if not, do standard greeting
        message += printLine() + " Hello! I'm " + BOT_NAME + "\n" + " What can I do for you?" + "\n";
        if (result.taskSize() > 0 || result.skipped() > 0) {
            message += printLine();
            message += String.format(" (Loaded %d tasks from disk%s)\n",
                    result.taskSize(),
                    result.skipped() > 0
                            ? String.format(", skipped %d corrupted lines",
                            result.skipped())
                            : "");
            message += result.tasks().list();
        } else {
            message += printLine();
            message += " There are currently no tasks in your list \n";
        }
        message += printLine();
        return message;
    }
}


