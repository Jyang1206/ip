import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

public class UberSuper {
    private static final String botName = "UberSuper";
    private static final String line = "_________________________________";
    private static final Scanner sc = new Scanner(System.in);
    private static ArrayList<Task> inputList = new ArrayList<Task>(100);

    // ===== Dates & Times =====
    private static final DateTimeFormatter DISPLAY_DATE = DateTimeFormatter.ofPattern("MMM dd yyyy");
    private static final DateTimeFormatter DISPLAY_DATETIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter STORAGE_DATE = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter STORAGE_DATETIME = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static void main(String[] args) {
        LoadedResult result = DataStorage.load();
        inputList = result.task;
        greet(result);
        echo();

        sc.close();
    }

    private static void printLine() {
        System.out.print(line + "\n");
    }

    private static void greet(LoadedResult result) {
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

    private static void goodBye() {
        System.out.print("Bye. Hope to see you again soon! \n");
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
            DataStorage.save(inputList);
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
            DataStorage.save(inputList);
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

    // ===== Date/Time parsing helpers =====

    private static LocalDateTime parseWhen(String raw) throws UberExceptions {
        String s = raw.trim();

        // 1) ISO date-time: 2019-12-02T18:00 (or "2019-12-02 18:00")
        try {
            if (s.contains("T")) return LocalDateTime.parse(s);
            if (s.matches("\\d{4}-\\d{2}-\\d{2}\\s+\\d{2}:\\d{2}")) {
                return LocalDateTime.parse(s.replace(' ', 'T'));
            }
        } catch (DateTimeParseException ignore) {}

        // 2) ISO date only: 2019-12-02  (treat as 00:00)
        try { return LocalDate.parse(s).atStartOfDay(); } catch (DateTimeParseException ignore) {}

        // 3) dd/MM/yyyy HHmm   e.g. 2/12/2019 1800
        try {
            DateTimeFormatter f = DateTimeFormatter.ofPattern("d/M/uuuu HHmm");
            return LocalDateTime.parse(s, f);
        } catch (DateTimeParseException ignore) {}

        // 4) dd/MM/yyyy        (00:00)
        try {
            DateTimeFormatter f = DateTimeFormatter.ofPattern("d/M/uuuu");
            return LocalDate.parse(s, f).atStartOfDay();
        } catch (DateTimeParseException ignore) {}

        // 5) d-M-uuuu HHmm
        try {
            DateTimeFormatter f = DateTimeFormatter.ofPattern("d-M-uuuu HHmm");
            return LocalDateTime.parse(s, f);
        } catch (DateTimeParseException ignore) {}

        // 6) d-M-uuuu (00:00)
        try {
            DateTimeFormatter f = DateTimeFormatter.ofPattern("d-M-uuuu");
            return LocalDate.parse(s, f).atStartOfDay();
        } catch (DateTimeParseException ignore) {}

        throw new UberExceptions("I couldn't understand the date/time: \"" + raw + "\".\n"
                + "Try formats like: 2019-12-02, 2019-12-02 18:00, 2/12/2019 1800.");
    }

    // Convenient display: if time is 00:00, show date only
    private static String display(LocalDateTime dt) {
        if (dt.toLocalTime().equals(LocalTime.MIDNIGHT)) {
            return dt.toLocalDate().format(DISPLAY_DATE);
        }
        return dt.format(DISPLAY_DATETIME);
    }

    private static void deadline(String input) {
        try {
            String[] parts = input.split("/");
            if (parts.length < 2) {
                throw new UberExceptions("Provide a proper deadline,");
            }
            String desc = parts[0].replaceFirst("deadline", "").trim();
            String[] p1 = parts[1].trim().split("\\s+", 2);
            if (p1.length < 2 || !p1[0].equalsIgnoreCase("by")) {
                throw new UberExceptions("Use format: deadline <desc> / by <time>");
            }
            String p2 = p1[1].trim();
            if (desc.isEmpty()) {
                throw new UberExceptions("Please provide a description");
            }
            LocalDateTime dl = parseWhen(p2);
            add(new Deadline(desc, dl));
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
            String fromPart = parts[1].trim(); // "from ..."
            String toPart   = parts[2].trim(); // "to ..."

            if (desc.isEmpty()) {
                throw new UberExceptions("Please describe the event");
            }
            //ensure correct formatting
            if (!fromPart.toLowerCase().startsWith("from") || !toPart.toLowerCase().startsWith("to")) {
                throw new UberExceptions("Use format: event <desc> /from <start> /to <end>");
            }

            LocalDateTime startTime = parseWhen(fromPart.substring(4).trim());
            LocalDateTime endTime = parseWhen(toPart.substring(2).trim());

            if (endTime.isBefore(startTime)) {
                throw new UberExceptions("End time cannot be before start time.");
            }
            add(new Event(desc, startTime, endTime));
        } catch (UberExceptions e) {
            printLine();
            System.out.print(e.getMessage() + "\n");
            printLine();
        }
    }
    private static void ondate(String input) {
        try {
            String[] parts = input.split("\\s+", 2);
            if (parts.length < 2) throw new UberExceptions("Use: ondate <yyyy-mm-dd | dd/MM/yyyy>");
            LocalDate day;
            String raw = parts[1].trim();
            try {
                day = LocalDate.parse(raw);
            } catch (DateTimeParseException ex) {
                try {
                    DateTimeFormatter f = DateTimeFormatter.ofPattern("d/M/uuuu");
                    day = LocalDate.parse(raw, f);
                } catch (DateTimeParseException e) {
                    throw new UberExceptions("Use: ondate <yyyy-mm-dd | dd/MM/yyyy>");
                }
            }

            printLine();
            System.out.println("Items on " + day.format(DISPLAY_DATE) + ":");
            int i = 1;
            for (Task t : inputList) {
                if (t instanceof Deadline) {
                    LocalDate d = ((Deadline) t).deadLine.toLocalDate();
                    if (d.equals(day)) System.out.println(i++ + ". " + t);
                } else if (t instanceof Event) {
                    Event ev = (Event) t;
                    // consider an event "occurring on" if any part of it touches that date
                    LocalDate s = ev.startTime.toLocalDate();
                    LocalDate e = ev.endTime.toLocalDate();
                    if (!day.isBefore(s) && !day.isAfter(e)) {
                        System.out.println(i++ + ". " + t);
                    }
                }
            }
            if (i == 1) System.out.println("(No items.)");
            printLine();
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
            DataStorage.save(inputList);
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

                    case ONDATE:
                        ondate(input);
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
        private final LocalDateTime deadLine;

        public Deadline(String description, LocalDateTime deadLine) {
            super(description, TaskType.DEADLINE);
            this.deadLine = deadLine;
        }

        @Override
        public String toString() {
            return String.format("[%s][%s] %s %s",
                    TaskType.DEADLINE.getSymbol(),
                    done() ? "X" : "",
                    desc(),
                    "(by: " + display(this.deadLine) + ")");
        }
        @Override
        public String formattedString() {
            return String.format("%s | %d | %s | %s",
                    type().getSymbol(),
                    done() ? 1 : 0,
                    desc(),
                    this.deadLine.format(STORAGE_DATETIME));
        }
    }

    private static class Event extends Task {
        private final LocalDateTime startTime;
        private final LocalDateTime endTime;

        public Event(String description, LocalDateTime startTime, LocalDateTime endTime) {
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
                    "(from: " + display(this.startTime) + ")",
                    "(to: " + display(this.endTime)) + ")";
        }
        @Override
        public String formattedString() {
            return String.format("%s | %d | %s | %s | %s",
                    type().getSymbol(),
                    done() ? 1 : 0,
                    desc(),
                    this.startTime.format((STORAGE_DATETIME)),
                    this.endTime.format((STORAGE_DATETIME)));
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
        ONDATE("ondate"),
        UNKNOWN("");

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
    private static class DataStorage {
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
                            case "T": {
                                Todo t = new Todo(description);
                                if (done == 1) {
                                    t.mark();
                                }
                                tasks.add(t);
                                break;
                            }
                            case "D":{
                                // incomplete prompt
                                if (parts.length < 4) {
                                    skipped++;
                                    break;
                                }
                                try {
                                    LocalDateTime deadline = LocalDateTime.parse(parts[3]);
                                    Deadline d = new Deadline(description, deadline);
                                    if (done == 1) {
                                        d.mark();
                                    }
                                    tasks.add(d);
                                } catch (DateTimeParseException e) {
                                    skipped++;
                                }
                                break;
                            }

                            case "E": {
                                if (parts.length < 5) { skipped++; break; }
                                try {
                                    LocalDateTime start = LocalDateTime.parse(parts[3]);
                                    LocalDateTime end   = LocalDateTime.parse(parts[4]);
                                    Event e = new Event(description, start, end);
                                    if (done == 1) e.mark();
                                    tasks.add(e);
                                } catch (DateTimeParseException ex) { skipped++; }
                                break;
                            }

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


