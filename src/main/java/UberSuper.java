import java.util.Scanner;
import java.util.ArrayList;

public class UberSuper {
    private static final String botName = "UberSuper";
    private static final String line = "_________________________________";
    private static final Scanner sc = new Scanner(System.in);
    private static ArrayList<String> inputList = new ArrayList<String>(100);

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
        int listNum = 1;
        while (sc.hasNextLine()) {
            String input = sc.nextLine().trim();
            if (input.equals("bye")) {
                goodBye();
                break;
            } else if (input.equals("list")) {
                printLine();
                for (String s : inputList) {
                    System.out.print(s);
                }
                printLine();
            } else {
                inputList.add(listNum + ". " + input + "\n");
                listNum++;
                printLine();
                System.out.print("added: " + input + "\n");
                printLine();
            }
        }
    }

}
