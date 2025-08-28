import java.util.Scanner;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import Utils.*;
import Exceptions.*;
import Tasks.*;

public class UberSuper {
    private static final String botName = "UberSuper";
    private static final String line = "_________________________________";

    private final Ui ui = new Ui(line);
    private final Scanner sc = new Scanner(System.in);

    private TaskList taskList = new TaskList();
    private final DataStorage storage = new DataStorage("uberSuper.txt");


    public UberSuper(String filePath) {
    }

    public void run() {
        Scanner sc = new Scanner(System.in);

        LoadedResult result = storage.load();

        taskList = result.task;
        ui.greet(result);
        echo();

    }

    public static void main(String[] args) {
        new UberSuper().run();
    }

}


