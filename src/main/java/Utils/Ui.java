package Utils;

import Exceptions.UberExceptions;
import Tasks.Task;
import Tasks.TaskList;

import java.util.Scanner;

public class Ui {
    private static final String botName = "UberSuper";
    private final Scanner sc;
    private static final String line = "_________________________________";
    private final TaskList tasks;

    public Ui(Scanner sc, TaskList tasks) {
        this.sc = sc;
        this.tasks = tasks;
    }

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
                        tasks.ondate(input);
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
    public static void printLine() {
        System.out.print(line + "\n");
    }

    public void goodBye() {
        System.out.print("Bye. Hope to see you again soon! \n");
        printLine();
    }

    public void greet(LoadedResult result) {
        // show result if available, if not, do standard greeting
        printLine();
        System.out.print(" Hello! I'm " + botName + "\n");
        System.out.print(" What can I do for you?" + "\n");
        if (result.taskSize() > 0 || result.skipped() > 0) {
            printLine();
            System.out.print(String.format(" (Loaded %d tasks from disk%s)\n",
                    result.taskSize(),
                    result.skipped() > 0
                            ? String.format(", skipped %d corrupted lines",
                            result.skipped())
                            : ""));
            result.task().list();
        } else {
            printLine();
            System.out.print(" There are currently no tasks in your list \n");
        }
        printLine();
    }

}
