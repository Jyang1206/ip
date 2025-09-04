package ubersuper;

import java.util.Scanner;

import ubersuper.exceptions.UberExceptions;
import ubersuper.tasks.TaskList;
import ubersuper.utils.DataStorage;
import ubersuper.utils.LoadedResult;
import ubersuper.utils.Parser;
import ubersuper.utils.command.CommandType;
import ubersuper.utils.ui.Ui;

/**
 * Entry point of the UberSuper application.
 * <p>
 * Starts up by loading tasks from disk, greeting the user, and entering the
 */

public class UberSuper {
    private final Scanner sc = new Scanner(System.in);
    private final DataStorage storage = new DataStorage("uberSuper.txt");
    private final LoadedResult result = storage.load();
    private final TaskList taskList = result.tasks();
    private final Ui ui = new Ui(taskList);
    private String commandType;

    public String greet() {
        return ui.greet(result);
    }

    public String getResponse(String input) {
        try {
            return ui.echo(input);
        } catch (UberExceptions e) {
            return "Error: " + e.getMessage();
        }
    }
    public String getCommandType() {
        return commandType;
    }
}


