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
            if(inputList.get(i - 1) == null) {
                throw new UberExceptions("There's no such task in the list");
            }
            Task t = inputList.get(i - 1);
            t.mark();
            printLine();
            System.out.print("Nice! I've marked this task as done: \n");
            System.out.print(t + "\n");
            printLine();
        } catch (UberExceptions e) {
            System.out.print(e.getMessage() + "\n");
        }
    }

    private static void unmark(String input) {
        String[] parts = input.split("\\s+", 2);
        int i = Integer.parseInt(parts[1]);
        try{
            if(inputList.get(i - 1) == null) {
                throw new UberExceptions("There's no such task in the list");
            }
            Task t = inputList.get(i - 1);
            t.unmark();
            printLine();
            System.out.print("Ok, I've marked this task as not done yet: \n");
            System.out.print(t + "\n");
            printLine();
        } catch (UberExceptions e) {
            System.out.print(e.getMessage() + "\n");
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

    private static void list(){
        printLine();
        int listNum = 1;
        System.out.print("Here are the tasks in your list:\n");
        for (Task task : inputList) {
            System.out.print(listNum + ". " + task + "\n");
            listNum++;
        }
        printLine();
    }
    private static void echo() {
        while (sc.hasNextLine()) {
            String input = sc.nextLine().trim();
            try {
                if (input.equals("bye")) {
                    goodBye();
                    break;
                } else if (input.equals("list")) {
                    list();
                } else if (input.startsWith("mark")) {
                    mark(input);
                } else if (input.startsWith("unmark")) {
                    unmark(input);
                } else if (input.startsWith("todo")) {
                    todo(input);
                } else if (input.startsWith("deadline")) {
                    deadline(input);
                } else if (input.startsWith("event")) {
                    event(input);
                } else {
                    throw new UberExceptions("What's up?");
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

        public Task(String description) {
            this.description = description;

        }
        public void mark() {this.isDone = true;}
        public void unmark() {this.isDone = false;}
        public boolean done() {return isDone;}
        public String desc() {return description;}
        @Override
        public String toString() {
            return String.format("[%s] %s", isDone ? "X" : "", description);
        }
    }

    private static class Todo extends Task {
        public Todo(String description) {
            super(description);
        }

        @Override
        public String toString() {
            return String.format("[T][%s] %s", done() ? "X" : "", desc());
        }

    }

    private static class Deadline extends Task {
        private final String deadLine;
        public Deadline(String description, String deadLine) {
            super(description);
            this.deadLine = deadLine;
        }

        @Override
        public String toString() {
            return String.format("[D][%s] %s %s", done() ? "X" : "", desc(), this.deadLine);
        }
    }
    private static class Event extends Task {
        private final String startTime;
        private final String endTime;
        public Event(String description, String startTime, String endTime) {
            super(description);
            this.startTime = startTime;
            this.endTime = endTime;
        }

        @Override
        public String toString() {
            return String.format("[E][%s] %s %s %s", done() ? "X" : "", desc(), this.startTime, this.endTime);
        }
    }

    private static class UberExceptions extends Exception {
        public UberExceptions(String message) {
            super(message);
        }
    }
}
