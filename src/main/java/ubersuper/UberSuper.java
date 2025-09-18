package ubersuper;

import ubersuper.clients.ClientList;
import ubersuper.exceptions.UberExceptions;
import ubersuper.tasks.TaskList;
import ubersuper.utils.LoadedResult;
import ubersuper.utils.storage.ClientStorage;
import ubersuper.utils.storage.TaskStorage;
import ubersuper.utils.ui.Ui;

import java.util.Scanner;

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
    private final Ui ui = new Ui(taskList, clientList);
    private String commandType;

    public String greet() {
        return ui.greet(tasksResult, clientsResult);
    }

    public String getResponse(String input) throws UberExceptions {
        return ui.echo(input);
    }

    public String getCommandType() {
        return commandType;
    }
}


