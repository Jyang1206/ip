package ubersuper.tasks;

import ubersuper.exceptions.UberExceptions;
import ubersuper.utils.DataStorage;
import ubersuper.utils.Parser;
import ubersuper.utils.ui.Ui;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

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
     * @param dataStorage storage component used to save/load the task list (maybe {@code null} in tests)
     */
    public TaskList(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
    }

    /**
     * Marks the i-th task as done (1-based index), saves the list, and prints a confirmation.
     *
     * @param input full user input line, e.g., {@code "mark 3"}
     * @return String message
     * @throws UberExceptions if the index is missing or out of range
     */
    public String mark(String input) {
        String message = "";
        String[] parts = input.split("\\s+", 2);
        int i = Integer.parseInt(parts[1]);

        try {
            if (i < 1 || i > this.size()) {
                throw new UberExceptions("There's no such task in the list");
            }
            Task t = this.get(i - 1);
            assert t != null : "Task retrieved for marking should not be null";
            t.mark();
            // save after changing done status
            dataStorage.save(this);
            message += Ui.printLine();
            message += "Nice! I've marked this task as done: \n";
            message += t + "\n";
            message += Ui.printLine();
        } catch (UberExceptions e) {
            return Ui.printLine() + e.getMessage() + "\n" + Ui.printLine();
        }
        return message;
    }

    /**
     * Marks the i-th task as not done (1-based index), saves the list, and prints a confirmation.
     *
     * @param input full user input line, e.g., {@code "unmark 2"}
     * @return String message
     * @throws UberExceptions if the index is missing or out of range
     */
    public String unmark(String input) {
        String message = "";
        String[] parts = input.split("\\s+", 2);
        int i = Integer.parseInt(parts[1]);
        try {
            if (i < 1 || i > this.size()) {
                throw new UberExceptions("There's no such task in the list");
            }
            Task t = this.get(i - 1);
            assert t != null : "Task retrieved for marking should not be null";
            t.unmark();
            dataStorage.save(this);
            message += Ui.printLine();
            message += "Ok, I've marked this task as not done yet: \n";
            message += t + "\n";
            message += Ui.printLine();
        } catch (UberExceptions e) {
            return Ui.printLine() + e.getMessage() + "\n" + Ui.printLine();
        }
        return message;
    }

    /**
     * Adds a {@link Todo} from the given input, saves the list, and prints a confirmation.
     * <p>Expected format: {@code "todo <description>"}.</p>
     *
     * @param input full user input line
     * @return String message
     * @throws UberExceptions if the description is missing/blank
     */
    public String todo(String input) {
        String message = "";
        try {
            String[] parts = input.split("\\s+", 2);
            if (parts.length < 2 || parts[1].trim().isEmpty()) {
                throw new UberExceptions("You forgot to include what you're supposed to do");
            }
            Todo t = new Todo(parts[1].trim());
            this.add(t);
            return this.save(t);
        } catch (UberExceptions e) {
            return Ui.printLine() + e.getMessage() + "\n" + Ui.printLine();
        }
    }

    /**
     * Stores the current list and prints the "added" confirmation for the provided task.
     *
     * @param t the task that was just added
     * @return String message
     */
    public String save(Task t) {
        assert t != null : "Task passed to save() must not be null";
        String message = "";
        dataStorage.save(this);
        message += String.format("You now have %d tasks in the list \n", this.size());
        message = Ui.printLine() + "Got it! I've added this task:\n" + t + "\n" + message + Ui.printLine();
        return message;
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
     * @return String message
     * @throws UberExceptions if the format is wrong or date-time cannot be parsed
     */
    public String deadline(String input) {
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
            assert dl != null : "Parsed deadline datetime should not be null";
            Deadline d = new Deadline(desc, dl);
            this.add(d);
            return this.save(d);
        } catch (UberExceptions e) {
            return Ui.printLine() + e.getMessage() + "\n" + Ui.printLine();
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
     * @return String message
     * @throws UberExceptions if the format is wrong, dates cannot be parsed, or end &lt; start
     */
    public String event(String input) {
        try {
            String[] parts = input.split("/");
            if (parts.length < 2) {
                throw new UberExceptions("There's nothing happening whenever");
            } else if (parts.length < 3) {
                throw new UberExceptions("So when does it end?");
            }

            String desc = parts[0].replaceFirst("event", "").trim();
            String fromPart = parts[1].trim(); // "from ..."
            String toPart = parts[2].trim(); // "to ..."

            if (desc.isEmpty()) {
                throw new UberExceptions("Please describe the event");
            }
            //ensure correct formatting
            if (!fromPart.toLowerCase().startsWith("from") || !toPart.toLowerCase().startsWith("to")) {
                throw new UberExceptions("Use format: event <desc> /from <start> /to <end>");
            }

            LocalDateTime startTime = Parser.parseWhen(fromPart.substring(4).trim());
            LocalDateTime endTime = Parser.parseWhen(toPart.substring(2).trim());

            assert startTime != null : "Event start time should not be null";
            assert endTime != null : "Event end time should not be null";

            if (endTime.isBefore(startTime)) {
                throw new UberExceptions("End time cannot be before start time.");
            }
            Event ev = new Event(desc, startTime, endTime);
            this.add(ev);
            return this.save(ev);
        } catch (UberExceptions e) {
            return Ui.printLine() + e.getMessage() + "\n" + Ui.printLine();
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
    public String onDate(String input) {
        String message = "";
        try {
            String[] parts = input.split("\\s+", 2);
            if (parts.length < 2) {
                throw new UberExceptions("Use: onDate <yyyy-mm-dd | dd/MM/yyyy>");
            }
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
            message += Ui.printLine();
            message += "Items on " + day.format(DateTimeFormatter.ofPattern("MMM dd yyyy")) + ": \n";
            int i = 1;
            for (Task t : this) {
                assert t != null : "Task in TaskList should not be null";
                if (t instanceof Deadline d) {
                    if (d.isOnDate(day)) {
                        message += i++ + ". " + t + "\n";
                    }
                } else if (t instanceof Event ev) {
                    // consider an event "occurring on" if any part of it touches that date
                    if (ev.isOnDate(day)) {
                        message += i++ + ". " + t + "\n";
                    }
                }
            }
            if (i == 1) {
                message += "(No items.)";
            }
            message += Ui.printLine();
        } catch (UberExceptions e) {
            return Ui.printLine() + e.getMessage() + "\n" + Ui.printLine();
        }
        return message;
    }

    /**
     * Returns a String of all tasks with their 1-based indices.
     */
    public String list() {
        String message = "";
        message += Ui.printLine();
        int listNum = 1;
        message += "Here are the tasks in your list:\n";
        for (Task task : this) {
            message += listNum + ". " + task + "\n";
            listNum++;
        }
        message += Ui.printLine();
        return message;
    }

    /**
     * Deletes the i-th task (1-based index), saves the list, and prints a confirmation.
     *
     * @param input full user input line, e.g., {@code "delete 1"}
     * @throws UberExceptions if the index is missing or out of range
     */
    public String delete(String input) {
        String message = "";
        String[] parts = input.split("\\s+", 2);
        int i = Integer.parseInt(parts[1]);
        try {
            if (i > this.size()) {
                throw new UberExceptions("You're deleting something that doesn't exist");
            }
            Task t = this.get(i - 1);
            this.remove(i - 1);
            dataStorage.save(this);
            message += String.format("You now have %d tasks in the list \n", this.size());
            message = Ui.printLine()
                    + "Ok, I've removed this task from the list: \n"
                    + t + "\n"
                    + message
                    + Ui.printLine();
        } catch (UberExceptions e) {
            return Ui.printLine() + e.getMessage() + "\n" + Ui.printLine();
        }
        return message;
    }

    /**
     * Finds and prints tasks whose descriptions contain any of the given keywords (case-insensitive).
     * Usage: {@code find <keyword(s)>}
     * Examples:
     * <pre>
     *   find book
     *   find return book
     * </pre>
     *
     * @return String message
     * Matching is OR across keywords: a task is listed if its description contains at least one keyword.
     */
    public String find(String input) {
        String message = "";
        String[] parts = input.split("\\s+", 2);
        if (parts.length < 2 || parts[1].isBlank()) {
            throw new UberExceptions("Use: find <keyword(s)>");
        }

        // Split the query into keywords and match, case-insensitive
        String[] keywords = parts[1].toLowerCase().split("\\s+");

        message += Ui.printLine() + "Here are the matching tasks in your list:\n";

        int i = 1;
        for (Task t : this) {
            assert t != null : "Tasks in TaskList should not be null";
            String lowerCaseDesc = t.desc().toLowerCase();
            for (String k : keywords) {
                if (!k.isBlank() && lowerCaseDesc.contains(k)) {
                    message += i++ + ". " + t + "\n";
                    break;
                }
            }
        }
        if (i == 1) {
            message += "(No matches.) \n";
        }
        message += Ui.printLine();
        return message;
    }
}
