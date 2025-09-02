package ubersuper.utils;

import ubersuper.exceptions.UberExceptions;
import ubersuper.tasks.TaskList;
import ubersuper.utils.command.CommandType;

import java.util.Scanner;

/**
 * Handles all user-facing I/O for the UberSuper app.
 * <p>
 * Responsibilities:
 * <ul>
 *   <li>Reading user commands from a {@link Scanner}</li>
 *   <li>Routing recognized commands to the appropriate {@link TaskList} methods</li>
 *   <li>Printing standard UI messages (greeting, divider line, goodbye, errors)</li>
 * </ul>
 * <p>
 * This class does not mutate storage directly; all task operations are delegated
 * to {@link TaskList}. The command parsing of the first token is handled by
 * {@link CommandType#fromInput(String)}.
 */
public class Ui {

    private static final String BOT_NAME = "UberSuper";
    private final Scanner sc;
    private static final String LINE = "_________________________________";
    private final TaskList tasks;

    /**
     * Constructs a {@code Ui} that will read commands from the given {@link Scanner}
     * and dispatch them to the provided {@link TaskList}.
     *
     * @param sc    input source for user commands (not closed by this class)
     * @param tasks the task list to operate on when handling commands
     */
    public Ui(Scanner sc, TaskList tasks) {
        this.sc = sc;
        this.tasks = tasks;
    }

    /**
     * Runs the main command loop.
     * <p>
     * Reads one line at a time from the {@link Scanner}, determines the command
     * using {@link CommandType#fromInput(String)}, and invokes the corresponding
     * operation on {@link TaskList}. The loop terminates when the user enters
     * the {@code bye} command, after printing a farewell message.
     * <p>
     * If a command is unknown or a handler throws an {@link UberExceptions},
     * an error message is printed and the loop continues to read the next line.
     */
    public void echo() {
        while (sc.hasNextLine()) {
            String input = sc.nextLine().trim();
            CommandType command = CommandType.fromInput(input);
            try {
                switch (command) {
                case BYE:
                    goodBye();
                    return;

                case LIST:
                    tasks.list();
                    break;

                case MARK:
                    tasks.mark(input);
                    break;

                case UNMARK:
                    tasks.unmark(input);
                    break;

                case TODO:
                    tasks.todo(input);
                    break;

                case DEADLINE:
                    tasks.deadline(input);
                    break;

                case EVENT:
                    tasks.event(input);
                    break;

                case DELETE:
                    tasks.delete(input);
                    break;

                case ONDATE:
                    tasks.onDate(input);
                    break;

                case UNKNOWN:
                default:
                    throw new UberExceptions("Sorry! I have no idea what you're trying to do.");
                }
            } catch (UberExceptions e) {
                Ui.printLine();
                System.out.print(e.getMessage() + "\n");
                Ui.printLine();
            }
        }
    }

    /**
     * Prints a standard horizontal divider line used by the UI.
     * <p>
     * This is a convenience utility so other classes can use the same divider.
     */

    public static void printLine() {
        System.out.print(LINE + "\n");
    }

    /**
     * Prints the farewell message and a divider line.
     * <p>
     * Typically invoked when the {@code bye} command is received.
     */
    public void goodBye() {
        System.out.print("Bye. Hope to see you again soon! \n");
        printLine();
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
    public void greet(LoadedResult result) {
        // show result if available, if not, do standard greeting
        printLine();
        System.out.print(" Hello! I'm " + BOT_NAME + "\n");
        System.out.print(" What can I do for you?" + "\n");
        if (result.taskSize() > 0 || result.skipped() > 0) {
            printLine();
            System.out.print(String.format(" (Loaded %d tasks from disk%s)\n",
                    result.taskSize(),
                    result.skipped() > 0
                            ? String.format(", skipped %d corrupted lines",
                            result.skipped())
                            : ""));
            result.tasks().list();
        } else {
            printLine();
            System.out.print(" There are currently no tasks in your list \n");
        }
        printLine();
    }


}
