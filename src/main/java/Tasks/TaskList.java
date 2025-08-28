package Tasks;

import Exceptions.UberExceptions;
import Utils.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public class TaskList {
    public ArrayList<Task> taskList = new ArrayList<Task>(100);

    public void add(Task task) {
        taskList.add(task);
        //save after adding to list
        DataStorage.save(taskList);
        String message = String.format("You now have %d tasks in the list \n", taskList.size());
        Ui.printLine();
        System.out.print("Got it! I've added this task:\n" + task + "\n" + message);
        Ui.printLine();
    }
    public void mark(String input) {
        String[] parts = input.split("\\s+", 2);
        int i = Integer.parseInt(parts[1]);

        try {
            if (i < 1 || i > taskList.size()) {
                throw new UberExceptions("There's no such task in the list");
            }
            Task t = taskList.get(i - 1);
            t.mark();
            // save after changing done status
            DataStorage.save(taskList);
            Ui.printLine();
            System.out.print("Nice! I've marked this task as done: \n");
            System.out.print(t + "\n");
            Ui.printLine();
        } catch (UberExceptions e) {
            Ui.printLine();
            System.out.print(e.getMessage() + "\n");
            Ui.printLine();
        }
    }

    public void unmark(String input) {
        String[] parts = input.split("\\s+", 2);
        int i = Integer.parseInt(parts[1]);
        try {
            if (i < 1 || i > taskList.size()) {
                throw new UberExceptions("There's no such task in the list");
            }
            Task t = taskList.get(i - 1);
            t.unmark();
            DataStorage.save(taskList);
            Ui.printLine();
            System.out.print("Ok, I've marked this task as not done yet: \n");
            System.out.print(t + "\n");
            Ui.printLine();
        } catch (UberExceptions e) {
            Ui.printLine();
            System.out.print(e.getMessage() + "\n");
            Ui.printLine();
        }

    }

    public void todo(String input) {

        try {
            String[] parts = input.split("\\s+", 2);
            if (parts.length < 2 || parts[1].trim().isEmpty()) {
                throw new UberExceptions("You forgot to include what you're supposed to do");
            }
            add(new Todo(parts[1].trim()));
        } catch (UberExceptions e) {
            Ui.printLine();
            System.out.print(e.getMessage() + "\n");
            Ui.printLine();
        }
    }

    public void deadline(String input) {
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
            Ui.printLine();
            System.out.print(e.getMessage() + "\n");
            Ui.printLine();
        }

    }

    public void event(String input) {
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
            Ui.printLine();
            System.out.print(e.getMessage() + "\n");
            Ui.printLine();
        }
    }
    public void ondate(String input) {
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

            Ui.printLine();
            System.out.println("Items on " + day.format(DISPLAY_DATE) + ":");
            int i = 1;
            for (Task t : taskList) {
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
            Ui.printLine();
        } catch (UberExceptions e) {
            Ui.printLine();
            System.out.print(e.getMessage() + "\n");
            Ui.printLine();
        }
    }

    public void list() {
        Ui.printLine();
        int listNum = 1;
        System.out.print("Here are the tasks in your list:\n");
        for (Task task : taskList) {
            System.out.print(listNum + ". " + task + "\n");
            listNum++;
        }
        Ui.printLine();
    }

    public void delete(String input) {
        String[] parts = input.split("\\s+", 2);
        int i = Integer.parseInt(parts[1]);
        try {
            if (i > taskList.size()) {
                throw new UberExceptions("You're deleting something that doesn't exist");
            }
            Task t = taskList.get(i - 1);
            taskList.remove(i - 1);
            DataStorage.save(taskList);
            String message = String.format("You now have %d tasks in the list \n", taskList.size());
            Ui.printLine();
            System.out.print("Ok, I've removed this task from the list: \n");
            System.out.print(t + "\n");
            System.out.print(message);
            Ui.printLine();
        } catch (UberExceptions e) {
            Ui.printLine();
            System.out.print(e.getMessage() + "\n");
            Ui.printLine();
        }

    }
}
