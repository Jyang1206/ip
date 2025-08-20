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
    }

    private static void mark(int i) {
        Task t = inputList.get(i);
        t.mark();
        printLine();
        System.out.print("Nice! I've marked this task as done: \n");
        System.out.print(t + "\n");
        printLine();
    }

    private static void unmark(int i) {
        Task t = inputList.get(i);
        t.unmark();
        printLine();
        System.out.print("Ok, I've marked this task as not done yet: \n");
        System.out.print(t + "\n");
        printLine();
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
            if (input.equals("bye")) {
                goodBye();
                break;
            } else if (input.equals("list")) {
                list();
            } else if (input.startsWith("mark ")) {
                String[] parts = input.split("\\s+", 2);
                int i = Integer.parseInt(parts[1]);
                mark(i);
            } else if (input.startsWith("unmark ")) {
                String[] parts = input.split("\\s+", 2);
                int i = Integer.parseInt(parts[1]);
                unmark(i);
            } else {
                add(new Task(input));
                printLine();
                System.out.print("added: " + input + "\n");
                printLine();
            }
        }
    }

    private static class Task {
        private boolean isDone = false;
        private final String description;

        public Task(String description){
            this.description = description;

        }
        public void mark() {this.isDone = true;}
        public void unmark() {this.isDone = false;}

        @Override
        public String toString(){
            return String.format("[%s] %s", isDone ? "X" : "", description);
        }
    }

}
