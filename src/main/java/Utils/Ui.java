package Utils;

import Tasks.Task;

public class Ui {
    private static final String botName = "UberSuper";

    private final String line = "_________________________________";


    public void printLine() {
        System.out.print(line + "\n");
    }

    public void goodBye() {
        System.out.print("Bye. Hope to see you again soon! \n");
        printLine();
    }

    public void greet(UberSuper.LoadedResult result) {
        // show result if available, if not, do standard greeting
        printLine();
        System.out.print(" Hello! I'm " + botName + "\n");
        System.out.print(" What can I do for you?" + "\n");
        if (result.taskSize > 0 || result.skipped > 0) {
            printLine();
            System.out.print(String.format(" (Loaded %d tasks from disk%s)\n",
                    result.taskSize,
                    result.skipped > 0
                            ? String.format(", skipped %d corrupted lines",
                            result.skipped)
                            : ""));
            list();
        } else {
            printLine();
            System.out.print(" There are currently no tasks in your list \n");
        }
        printLine();
    }
    private static void add(Task task) {
        inputList.add(task);
        //save after adding to list
        DataStorage.save(inputList);
        String message = String.format("You now have %d tasks in the list \n", inputList.size());
        printLine();
        System.out.print("Got it! I've added this task:\n" + task + "\n" + message);
        printLine();
    }
}
