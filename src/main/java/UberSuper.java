import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class UberSuper {
    private static final String botName = "UberSuper";
    private static final String line = "_________________________________";
    private static final Scanner sc = new Scanner(System.in);
    private static ArrayList<Task> inputList = new ArrayList<Task>(100);

    public static void main(String[] args) {
        greet();
        echo();
    }

    private static void printLine() {
        System.out.print(line + "\n");
    }

    private static void greet() {
        printLine();
        System.out.print(" Hello! I'm " + botName + "\n");
        System.out.print(" What can I do for you?" + "\n");
        printLine();

    }

    private static void goodBye() {
        System.out.print("Bye. Hope to see you again soon! \n");
        printLine();
    }

    private static void add(Task task) {
        inputList.add(task);
        //save after adding to list
        dataStorage.save(inputList);
        String message = String.format("You now have %d tasks in the list \n", inputList.size());
        printLine();
        System.out.print("Got it! I've added this task:\n" + task + "\n" + message);
        printLine();
    }

    private static void mark(String input) {
        String[] parts = input.split("\\s+", 2);
        int i = Integer.parseInt(parts[1]);

        try {
            if (i < 1 || i > inputList.size()) {
                throw new UberExceptions("There's no such task in the list");
            }
            Task t = inputList.get(i - 1);
            t.mark();
            // save after changing done status
            dataStorage.save(inputList);
            printLine();
            System.out.print("Nice! I've marked this task as done: \n");
            System.out.print(t + "\n");
            printLine();
        } catch (UberExceptions e) {
            printLine();
            System.out.print(e.getMessage() + "\n");
            printLine();
        }
    }

    private static void unmark(String input) {
        String[] parts = input.split("\\s+", 2);
        int i = Integer.parseInt(parts[1]);
        try {
            if (i < 1 || i > inputList.size()) {
                throw new UberExceptions("There's no such task in the list");
            }
            Task t = inputList.get(i - 1);
            t.unmark();
            dataStorage.save(inputList);
            printLine();
            System.out.print("Ok, I've marked this task as not done yet: \n");
            System.out.print(t + "\n");
            printLine();
        } catch (UberExceptions e) {
            printLine();
            System.out.print(e.getMessage() + "\n");
            printLine();
        }

    }

    private static void todo(String input) {

        try {
            String[] parts = input.split("\\s+", 2);
            if (parts.length < 2 || parts[1].trim().isEmpty()) {
                throw new UberExceptions("You forgot to include what you're supposed to do");
            }
            add(new Todo(parts[1].trim()));
        } catch (UberExceptions e) {
            printLine();
            System.out.print(e.getMessage() + "\n");
            printLine();
        }
    }

    private static void deadline(String input) {
        try {
            String[] parts = input.split("/");
            if (parts.length < 2) {
                throw new UberExceptions("Provide a proper deadline,");
            }
            String desc = parts[0].replaceFirst("deadline", "").trim();
            String[] p1 = parts[1].trim().split("\\s+", 2);
            if (p1.length < 2 || p1[0].equalsIgnoreCase("by")) {
                throw new UberExceptions("Use format: deadline <desc> / by <time>");
            }
            String dl = parts[1].replaceFirst("by", "(by:");
            if (desc.isEmpty()) {
                throw new UberExceptions("Please provide a description");
            }
            add(new Deadline(desc, dl + ")"));
        } catch (UberExceptions e) {
            printLine();
            System.out.print(e.getMessage() + "\n");
            printLine();
        }

    }

    private static void event(String input) {
        try {
            String[] parts = input.split("/");
            if (parts.length < 2) {
                throw new UberExceptions("There's nothing happening whenever");
            } else if (parts.length < 3) {
                throw new UberExceptions("So when does it end?");
            }
            String desc = parts[0].replaceFirst("event", "").trim();
            String[] p1 = parts[1].trim().split("\\s+", 2);
            String[] p2 = parts[2].trim().split("\\s+", 2);

            //ensure correct formatting
            if (p1.length < 2 || !p1[0].equalsIgnoreCase("from")) {
                throw new UberExceptions("Use format: event <desc> /from <start> /to <end>");
            }
            if (p2.length < 2 || !p2[0].equalsIgnoreCase("to")) {
                throw new UberExceptions("Use format: event <desc> /from <start> /to <end>");
            }
            String startTime = parts[1].replaceFirst("from", "(from:");
            String endTime = parts[2].replaceFirst("to", "to:");

            if (desc.isEmpty()) {
                throw new UberExceptions("Please describe the event");
            }
            add(new Event(desc, startTime, endTime + ")"));
        } catch (UberExceptions e) {
            printLine();
            System.out.print(e.getMessage() + "\n");
            printLine();
        }
    }

    private static void list() {
        printLine();
        int listNum = 1;
        System.out.print("Here are the tasks in your list:\n");
        for (Task task : inputList) {
            System.out.print(listNum + ". " + task + "\n");
            listNum++;
        }
        printLine();
    }

    private static void delete(String input) {
        String[] parts = input.split("\\s+", 2);
        int i = Integer.parseInt(parts[1]);
        try {
            if (i > inputList.size()) {
                throw new UberExceptions("You're deleting something that doesn't exist");
            }
            Task t = inputList.get(i - 1);
            inputList.remove(i - 1);
            String message = String.format("You now have %d tasks in the list \n", inputList.size());
            printLine();
            System.out.print("Ok, I've removed this task from the list: \n");
            System.out.print(t + "\n");
            System.out.print(message);
            printLine();
        } catch (UberExceptions e) {
            printLine();
            System.out.print(e.getMessage() + "\n");
            printLine();
        }

    }

    private static void echo() {
        while (sc.hasNextLine()) {
            String input = sc.nextLine().trim();
            CommandType command = CommandType.fromInput(input);
            try {
                switch (command) {
                    case BYE:
                        goodBye();
                        return;

                    case LIST:
                        list();
                        break;

                    case MARK:
                        mark(input);
                        break;

                    case UNMARK:
                        unmark(input);
                        break;

                    case TODO:
                        todo(input);
                        break;

                    case DEADLINE:
                        deadline(input);
                        break;

                    case EVENT:
                        event(input);
                        break;

                    case DELETE:
                        delete(input);
                        break;

                    case UNKNOWN:
                    default:
                        throw new UberExceptions("Sorry! I have no idea what you're trying to do.");
                }
            } catch (UberExceptions e) {
                printLine();
                System.out.print(e.getMessage() + "\n");
                printLine();
            }
        }
    }

    private static abstract class Task {
        private boolean isDone = false;
        private final String description;
        private final TaskType type;

        abstract public String formattedString();

        public Task(String description, TaskType type) {
            this.description = description;
            this.type = type;

        }

        public void mark() {
            this.isDone = true;
        }

        public void unmark() {
            this.isDone = false;
        }

        public boolean done() {
            return isDone;
        }

        public String desc() {
            return description;
        }

        public TaskType type() {
            return type;
        }
        @Override
        public String toString() {
            return String.format("[%s][%s] %s",
                    type.getSymbol(),
                    isDone ? "X" : "",
                    description);
        }
    }

    private static class Todo extends Task {
        public Todo(String description) {
            super(description, TaskType.TODO);
        }

        @Override
        public String formattedString() {
            return String.format("%s | %d | %s",
                    type().getSymbol(),
                    done() ? 1 : 0,
                    desc());
        }
    }

    private static class Deadline extends Task {
        private final String deadLine;

        public Deadline(String description, String deadLine) {
            super(description, TaskType.DEADLINE);
            this.deadLine = deadLine;
        }

        @Override
        public String toString() {
            return String.format("[%s][%s] %s %s",
                    TaskType.DEADLINE.getSymbol(),
                    done() ? "X" : "",
                    desc(),
                    this.deadLine);
        }
        @Override
        public String formattedString() {
            return String.format("%s | %d | %s | %s",
                    type().getSymbol(),
                    done() ? 1 : 0,
                    desc(),
                    deadLine);
        }
    }

    private static class Event extends Task {
        private final String startTime;
        private final String endTime;

        public Event(String description, String startTime, String endTime) {
            super(description, TaskType.EVENT);
            this.startTime = startTime;
            this.endTime = endTime;
        }

        @Override
        public String toString() {
            return String.format("[%s][%s] %s %s %s",
                    TaskType.EVENT.getSymbol(),
                    done() ? "X" : "",
                    desc(),
                    this.startTime,
                    this.endTime);
        }
        @Override
        public String formattedString() {
            return String.format("%s | %d | %s | %s | %s",
                    type().getSymbol(),
                    done() ? 1 : 0,
                    desc(),
                    this.startTime,
                    this.endTime);
        }
    }

    //exceptions from chatBot
    private static class UberExceptions extends Exception {
        public UberExceptions(String message) {
            super(message);
        }
    }

    private enum TaskType {
        TODO("T"),
        DEADLINE("D"),
        EVENT("E");

        private final String symbol;

        TaskType(String symbol) {
            this.symbol = symbol;
        }

        public String getSymbol() {
            return symbol;
        }

    }

    private enum CommandType {
        BYE("bye"),
        LIST("list"),
        MARK("mark"),
        UNMARK("unmark"),
        TODO("todo"),
        DEADLINE("deadline"),
        EVENT("event"),
        DELETE("delete"),
        UNKNOWN("");   // fallback

        private final String keyword;

        CommandType(String keyword) {
            this.keyword = keyword;
        }

        public String getKeyword() {
            return keyword;
        }

        public static CommandType fromInput(String input) {
            for (CommandType c : values()) {
                if (input.startsWith(c.keyword)) {
                    return c;
                }
            }
            return UNKNOWN;
        }
    }

    // handles saving and loading logic
    // Format: [TaskType] | [Status] | Description | Date/Time
    private static class dataStorage {
        private static final Path DATA_PATH = Paths.get("data", "uberSuper.txt");

        static LoadedResult load() {
            ArrayList<Task> tasks = new ArrayList<>();
            int skipped = 0;
            try {
                if (Files.notExists(DATA_PATH.getParent())) {
                    Files.createDirectories(DATA_PATH.getParent());
                }
                if (Files.notExists(DATA_PATH)) {
                    Files.createFile(DATA_PATH);
                    return new LoadedResult(tasks, 0, 0);
                }
                List<String> lines = Files.readAllLines(DATA_PATH, StandardCharsets.UTF_8);
                for (String data : lines) {
                    String line = data.trim();
                    if (line.isEmpty()) continue;
                    String[] parts = line.split("\\|");
                    for (int i = 0; i < parts.length; i++) {
                        parts[i] = parts[i].trim();
                    }

                    try {
                        // incomplete prompt
                        if (parts.length < 3) {
                            skipped++;
                            continue;
                        }
                        String type = parts[0];
                        int done = Integer.parseInt(parts[1]);
                        String description = parts[2];
                        switch (type) {
                            case "T":
                                Todo t = new Todo(description);
                                if (done == 1) {
                                    t.mark();
                                }
                                tasks.add(t);
                                break;
                            case "D":
                                // incomplete prompt
                                if (parts.length < 4) {
                                    skipped++;
                                    break;
                                }
                                Deadline d = new Deadline(description, parts[3]);
                                if (done == 1) {
                                    d.mark();
                                }
                                tasks.add(d);
                                break;
                            case "E":
                                // incomplete prompt
                                if (parts.length < 5) {
                                    skipped++;
                                    break;
                                }
                                Event e = new Event(description, parts[3], parts[4]);
                                if (done == 1) {
                                    e.mark();
                                }
                                tasks.add(e);
                                break;

                            default:
                                // task not labelled correctly
                                skipped++;
                        }
                    } catch (Exception e) {
                        skipped++;
                    }
                }
                return new LoadedResult(tasks, tasks.size(), skipped);
            } catch (IOException ioe) {
                return new LoadedResult(tasks, 0, 0);
            }
        }

        public static void save(ArrayList<Task> tasks) {
            try {
                if (Files.notExists(DATA_PATH.getParent())) {
                    Files.createDirectories(DATA_PATH.getParent());
                }
                List<String> lines = tasks.stream()
                        .map(Task::formattedString)
                        .collect(Collectors.toList());
                Files.write(DATA_PATH, lines, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            } catch (IOException ioe) {
                System.out.print("Could not save tasks!");
            }
        }
    }

    private static class LoadedResult {
        private final ArrayList<Task> task;
        private final int taskSize;
        private final int skipped;

        public LoadedResult(ArrayList<Task> task, int taskSize, int skipped) {
            this.task = task;
            this.taskSize = taskSize;
            this.skipped = skipped;
        }
    }
}


