import java.util.Scanner;

import Utils.*;
import Tasks.*;

public class UberSuper {
    private final Scanner sc = new Scanner(System.in);
    private TaskList taskList = new TaskList();
    private final Ui ui = new Ui(sc, taskList);
    private final DataStorage storage = new DataStorage("uberSuper.txt");


    public UberSuper() {
    }

    public void run() {
        LoadedResult result = storage.load();
        taskList = result.task();
        ui.greet(result);
        ui.echo();

    }

    public static void main(String[] args) {
        new UberSuper().run();
    }

}


