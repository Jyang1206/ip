import java.util.Scanner;
import java.util.ArrayList;

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
        String message = String.format("You now have %d tasks in the list \n", inputList.size());
        printLine();
        System.out.print("Got it! I've added this task:\n" + task + "\n" + message);
        printLine();
    }

    private static void mark(String input) {
        String[] parts = input.split("\\s+", 2);
        int i = Integer.parseInt(parts[1]);

        try {
            if(i > inputList.size()) {
                throw new UberExceptions("There's no such task in the list");
            }
            Task t = inputList.get(i - 1);
            t.mark();
            printLine();
            System.out.print("Nice! I've marked this task as done: \n");
            System.out.print(t + "\n");
            printLine();
        } catch (UberExceptions e) {
            printLine();
            System.out.print(e.getMessage() + "\n");
            printLine();        }
    }

    private static void unmark(String input) {
        String[] parts = input.split("\\s+", 2);
        int i = Integer.parseInt(parts[1]);
        try{
            if(i > inputList.size()) {
                throw new UberExceptions("There's no such task in the list");
            }
            Task t = inputList.get(i - 1);
            t.unmark();
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

    private static void todo(String input){

        try {
            String[] parts = input.split("\\s+", 2);
            if (parts.length < 2) {
                throw new UberExceptions("You forgot to include what you're supposed to do");
            }
            add(new Todo(parts[1]));
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
            String dl = parts[1].replaceFirst("by", "(by:");
            add(new Deadline(desc, dl + ")"));
        } catch (UberExceptions e){
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
            String startTime = parts[1].replaceFirst("from", "(from:");
            String endTime = parts[2].replaceFirst("to", "to:");
            add(new Event(desc, startTime, endTime + ")"));
        } catch (UberExceptions e){
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
        try{
            if(i > inputList.size()) {
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

    private static class Task {
        private boolean isDone = false;
        private final String description;
        private final TaskType type;

        public Task(String description, TaskType type) {
            this.description = description;
            this.type = type;

        }
        public void mark() {this.isDone = true;}
        public void unmark() {this.isDone = false;}
        public boolean done() {return isDone;}
        public String desc() {return description;}
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
    }

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
}
