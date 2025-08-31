package ubersuper.tasks;

import ubersuper.exceptions.UberExceptions;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

import ubersuper.utils.DataStorage;
import ubersuper.utils.Parser;
import ubersuper.utils.Ui;

/**
 * Mutable list of {@link Task} items plus high-level operations used by the UI.
 * <p>
 * Responsibilities:
 * <ul>
 *   <li>Holds tasks in memory (extends {@code ArrayList<Task>}).</li>
 *   <li>Implements command behaviors: {@code list}, {@code todo}, {@code deadline},
 *       {@code event}, {@code delete}, {@code mark}, {@code unmark}, {@code onDate}.</li>
 *   <li>Stores changes to {@link DataStorage} after any state change.</li>
 *   <li>Prints user-facing messages (divider lines are handled by {@link Ui}).</li>
 * </ul>
 */
public class TaskList extends ArrayList<Task> {
    private final DataStorage dataStorage;

    /**
     * Creates a {@code TaskList} bound to a storage backend.
     *
     * @param dataStorage storage component used to save/load the task list (may be {@code null} in tests)
     */
    public TaskList(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
    }

    /**
     * Marks the i-th task as done (1-based index), saves the list, and prints a confirmation.
     *
     * @param input full user input line, e.g., {@code "mark 3"}
     * @throws UberExceptions if the index is missing or out of range
     */
    public void mark(String input) {
        String[] parts = input.split("\\s+", 2);
        int i = Integer.parseInt(parts[1]);

        try {
            if (i < 1 || i > this.size()) {
                throw new UberExceptions("There's no such task in the list");
            }
            Task t = this.get(i - 1);
            t.mark();
            // save after changing done status
            dataStorage.save(this);
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
    /**
     * Marks the i-th task as not done (1-based index), saves the list, and prints a confirmation.
     *
     * @param input full user input line, e.g., {@code "unmark 2"}
     * @throws UberExceptions if the index is missing or out of range
     */
    public void unmark(String input) {
        String[] parts = input.split("\\s+", 2);
        int i = Integer.parseInt(parts[1]);
        try {
            if (i < 1 || i > this.size()) {
                throw new UberExceptions("There's no such task in the list");
            }
            Task t = this.get(i - 1);
            t.unmark();
            dataStorage.save(this);
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

    /**
     * Adds a {@link Todo} from the given input, saves the list, and prints a confirmation.
     * <p>Expected format: {@code "todo <description>"}.</p>
     *
     * @param input full user input line
     * @throws UberExceptions if the description is missing/blank
     */
    public void todo(String input) {
        try {
            String[] parts = input.split("\\s+", 2);
            if (parts.length < 2 || parts[1].trim().isEmpty()) {
                throw new UberExceptions("You forgot to include what you're supposed to do");
            }
            Todo t = new Todo(parts[1].trim());
            this.add(t);
            this.save(t);
        } catch (UberExceptions e) {
            Ui.printLine();
            System.out.print(e.getMessage() + "\n");
            Ui.printLine();
        }
    }
    /**
     * Stores the current list and prints the "added" confirmation for the provided task.
     *
     * @param t the task that was just added
     */
    public void save(Task t) {
        dataStorage.save(this);
        String message = String.format("You now have %d tasks in the list \n", this.size());
        Ui.printLine();
        System.out.print("Got it! I've added this task:\n" + t + "\n" + message);
        Ui.printLine();
    }

    /**
     * Adds a {@link Deadline} parsed from user input, saves the list, and prints a confirmation.
     * <p>Expected format: {@code "deadline <desc> /by <when>"}.</p>
     * <p>
     * Supported {@code <when>} formats are delegated to {@link Parser#parseWhen(String)} and include:
     * {@code yyyy-MM-dd}, {@code yyyy-MM-dd HH:mm}, {@code yyyy-MM-dd'T'HH:mm}, {@code d/M/uuuu [HHmm]},
     * {@code d-M-uuuu [HHmm]}.
     * </p>
     *
     * @param input full user input line
     * @throws UberExceptions if the format is wrong or date-time cannot be parsed
     */
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
            LocalDateTime dl = Parser.parseWhen(p2);
            Deadline d = new Deadline(desc, dl);
            add(d);
            save(d);
        } catch (UberExceptions e) {
            Ui.printLine();
            System.out.print(e.getMessage() + "\n");
            Ui.printLine();
        }

    }

    /**
     * Adds an {@link Event} parsed from user input, saves the list, and prints a confirmation.
     * <p>Expected format: {@code "event <desc> /from <start> /to <end>"}.</p>
     * <p>
     * Date-time parsing is delegated to {@link Parser#parseWhen(String)} and supports the same formats
     * as {@link #deadline(String)}. The end time must not be before the start time.
     * </p>
     *
     * @param input full user input line
     * @throws UberExceptions if the format is wrong, dates cannot be parsed, or end &lt; start
     */
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

            LocalDateTime startTime = Parser.parseWhen(fromPart.substring(4).trim());
            LocalDateTime endTime = Parser.parseWhen(toPart.substring(2).trim());

            if (endTime.isBefore(startTime)) {
                throw new UberExceptions("End time cannot be before start time.");
            }
            Event ev = new Event(desc, startTime, endTime);
            add(ev);
            save(ev);
        } catch (UberExceptions e) {
            Ui.printLine();
            System.out.print(e.getMessage() + "\n");
            Ui.printLine();
        }
    }

    /**
     * Lists deadlines/events that occur on a specific date (Todos are ignored).
     * <p>Expected formats: {@code onDate yyyy-MM-dd} or {@code onDate d/M/uuuu}.</p>
     * <p>
     * Behavior:
     * <ul>
     *   <li>Deadlines match if their date equals the given day.</li>
     *   <li>Events match if the day overlaps the inclusive range
     *       {@code [start.toLocalDate(), end.toLocalDate()] }.</li>
     *   <li>Prints a header, then matching items numbered from 1; prints "(No items.)" if none.</li>
     * </ul>
     * </p>
     *
     * @param input full user input line, e.g., {@code "onDate 2019-12-02"}
     * @throws UberExceptions if the date cannot be parsed
     */
    public void onDate(String input) {
        try {
            String[] parts = input.split("\\s+", 2);
            if (parts.length < 2) throw new UberExceptions("Use: onDate <yyyy-mm-dd | dd/MM/yyyy>");
            LocalDate day;
            String raw = parts[1].trim();
            try {
                day = LocalDate.parse(raw);
            } catch (DateTimeParseException ex) {
                try {
                    DateTimeFormatter f = DateTimeFormatter.ofPattern("d/M/uuuu");
                    day = LocalDate.parse(raw, f);
                } catch (DateTimeParseException e) {
                    throw new UberExceptions("Use: onDate <yyyy-mm-dd | dd/MM/yyyy>");
                }
            }

            Ui.printLine();
            System.out.println("Items on " + day.format(DateTimeFormatter.ofPattern("MMM dd yyyy")) + ":");
            int i = 1;
            for (Task t : this) {
                if (t instanceof Deadline d) {
                    if (d.onDate(day, i)) {
                        i++;
                    }
                } else if (t instanceof Event ev) {
                    // consider an event "occurring on" if any part of it touches that date
                    if (ev.onDate(day, i)) {
                        i++;
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

    /**
     * Prints all tasks with their 1-based indices.
     */
    public void list() {
        Ui.printLine();
        int listNum = 1;
        System.out.print("Here are the tasks in your list:\n");
        for (Task task : this) {
            System.out.print(listNum + ". " + task + "\n");
            listNum++;
        }
        Ui.printLine();
    }

    /**
     * Deletes the i-th task (1-based index), saves the list, and prints a confirmation.
     *
     * @param input full user input line, e.g., {@code "delete 1"}
     * @throws UberExceptions if the index is missing or out of range
     */
    public void delete(String input) {
        String[] parts = input.split("\\s+", 2);
        int i = Integer.parseInt(parts[1]);
        try {
            if (i > this.size()) {
                throw new UberExceptions("You're deleting something that doesn't exist");
            }
            Task t = this.get(i - 1);
            this.remove(i - 1);
            dataStorage.save(this);
            String message = String.format("You now have %d tasks in the list \n", this.size());
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
