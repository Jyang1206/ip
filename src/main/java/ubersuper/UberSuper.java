package ubersuper;

import ubersuper.tasks.TaskList;
import ubersuper.utils.DataStorage;
import ubersuper.utils.LoadedResult;
import ubersuper.utils.Ui;

import java.util.Scanner;

/**
 * Entry point of the UberSuper application.
 * <p>
 * Starts up by loading tasks from disk, greeting the user, and entering the
 * command loop handled by {@link ubersuper.utils.Ui#echo()}.
 */

public class UberSuper {
    private final Scanner sc = new Scanner(System.in);
    private final DataStorage storage = new DataStorage("uberSuper.txt");
    private final LoadedResult result = storage.load();
    private final TaskList taskList = result.tasks();
    private final Ui ui = new Ui(sc, taskList);

    /**
     * Runs the application: shows the greeting and processes user commands
     * until the {@code bye} command is entered.
     */

    public void run() {
        ui.greet(result);
        ui.echo();
    }

    /**
     * Launches UberSuper.
     *
     * @param args command-line arguments (unused)
     */

    public static void main(String[] args) {
        new UberSuper().run();
    }

}


