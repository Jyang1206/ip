package ubersuper;

import java.util.Scanner;

import ubersuper.clients.ClientList;
import ubersuper.exceptions.UberExceptions;
import ubersuper.tasks.Task;
import ubersuper.tasks.TaskList;
import ubersuper.utils.storage.ClientStorage;
import ubersuper.utils.storage.DataStorage;
import ubersuper.utils.LoadedResult;
import ubersuper.utils.storage.TaskStorage;
import ubersuper.utils.ui.Ui;

/**
 * Entry point of the UberSuper application.
 * <p>
 * Starts up by loading tasks from disk, greeting the user, and entering the
 */

public class UberSuper {
    private final Scanner sc = new Scanner(System.in);
    private final TaskStorage taskStorage = new TaskStorage();
    private final ClientStorage clientStorage = new ClientStorage();
    private final LoadedResult<TaskList> tasksResult = taskStorage.load();
    private final LoadedResult<ClientList> clientsResult = clientStorage.load();
    private final TaskList taskList = tasksResult.list();
    private final ClientList clientList = clientsResult.list();
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


