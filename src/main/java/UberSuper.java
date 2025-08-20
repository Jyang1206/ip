import java.util.Scanner;

public class UberSuper {
    private static final String botName = "UberSuper";
    private static final String line = "_________________________________";
    private static final Scanner sc = new Scanner(System.in);

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
    private static void echo() {
        while (sc.hasNextLine()) {
            String input = sc.nextLine().trim();
            if (input.equals("bye")) {
                goodBye();
                break;
            } else {
                printLine();
                System.out.print(input + "\n");
                printLine();
            }
        }
    }

}
