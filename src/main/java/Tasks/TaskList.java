package Tasks;

import Exceptions.UberExceptions;
import Utils.CommandType;
import Utils.DataStorage;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public class TaskList {
    private static ArrayList<Task> taskList = new ArrayList<Task>(100);

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


}
