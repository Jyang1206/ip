package ubersuper.utils.ui;

import ubersuper.clients.ClientList;
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
 * {@link Parser#fromInput(String)}.
 */
public class Ui {

    private static final String BOT_NAME = "UberSuper";
    private static final String LINE = "_________________________________";
    private final TaskList tasks;
    private final ClientList clients;

    /**
     * @param tasks the task list to operate on when handling commands
     */
    public Ui(TaskList tasks, ClientList clients) {
        assert tasks != null : "Ui must be created with a non-null TaskList";
        this.tasks = tasks;
        assert clients != null : "Ui must be created with a non-null ClientList";
        this.clients = clients;
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
    public String echo(String raw) throws UberExceptions {
        String input = raw.trim();
        CommandType command = Parser.fromInput(input);
        assert command != null : "Parser must return a valid CommandType";

        switch (command) {
        case BYE:
            return goodBye();
        case TASKLIST:
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
        case DELETETASK:
            return tasks.delete(input);
        case ONDATE:
            return tasks.onDate(input);
        case FINDTASK:
            return tasks.find(input);
        case FINDCLIENT:
            return clients.find(input);
        case DELETECLIENT:
            return clients.delete(input);
        case CLIENTLIST:
            return clients.list();
        case ADDCLIENT:
            return clients.add(input);
        case UNKNOWN:
        default:
            throw new UberExceptions("Sorry! I have no idea what you're trying to do.");
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
        return "Bye. Hope to see you again soon! \n";
    }


    /**
     * Prints the initial greeting and, if applicable, a summary of the load tasksResults.
     * <p>
     * When prior tasks are found on disk, shows how many were loaded and how many
     * lines were skipped due to errors, then prints the current list of tasks.
     * Otherwise, informs the user that the list is empty.
     *
     * @param tasksResult   the outcome of loading tasks from disk
     * @param clientsResult the outcome of loading clients from disk
     */
    public String greet(LoadedResult<TaskList> tasksResult, LoadedResult<ClientList> clientsResult) {
        String message = "";
        // show tasksResult if available, if not, do standard greeting
        message += " Hello! I'm " + BOT_NAME + "\n" + " What can I do for you?" + "\n";
        if (tasksResult.listSize() > 0 || tasksResult.skipped() > 0) {

            message += String.format(" (Loaded %d tasks from disk%s)\n",
                    tasksResult.listSize(),
                    tasksResult.skipped() > 0
                            ? String.format(", skipped %d corrupted lines",
                            tasksResult.skipped())
                            : "");
            message += tasksResult.list().list();
        } else {
            message += " There are currently no tasks in your list \n";
        }
        message += printLine();

        // show clientsResult if available, if not, do standard greeting
        if (clientsResult.listSize() > 0 || clientsResult.skipped() > 0) {
            message += String.format(" (Loaded %d clients from disk%s)\n",
                    clientsResult.listSize(),
                    clientsResult.skipped() > 0
                            ? String.format(", skipped %d corrupted lines",
                            clientsResult.skipped())
                            : "");
            message += clientsResult.list().list();
        } else {
            message += " There are currently no clients in your list \n";
        }
        return message;
    }
}


