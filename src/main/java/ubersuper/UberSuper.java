package ubersuper;

import java.util.Scanner;

import ubersuper.tasks.TaskList;
import ubersuper.utils.DataStorage;
import ubersuper.utils.LoadedResult;
import ubersuper.utils.Ui;

public class UberSuper {
    private final Scanner sc = new Scanner(System.in);
    private final DataStorage storage = new DataStorage("uberSuper.txt");
    private final LoadedResult result = storage.load();
    private TaskList taskList = result.tasks();
    private final Ui ui = new Ui(sc, taskList);


    public void run() {
        ui.greet(result);
        ui.echo();
    }

    public static void main(String[] args) {
        new UberSuper().run();
    }

}


