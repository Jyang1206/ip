import java.util.Scanner;

import Utils.*;
import Tasks.*;

public class UberSuper {
    private final Scanner sc = new Scanner(System.in);
    private final DataStorage storage = new DataStorage("uberSuper.txt");
    private final LoadedResult result = storage.load();
    private TaskList taskList = result.tasks();
    private final Ui ui = new Ui(sc, taskList);


    public UberSuper() {
    }

    public void run() {

        ui.greet(result);
        ui.echo();

    }

    public static void main(String[] args) {
        new UberSuper().run();
    }

}


