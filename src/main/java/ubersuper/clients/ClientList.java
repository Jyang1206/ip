package ubersuper.clients;

import ubersuper.exceptions.UberExceptions;
import ubersuper.tasks.Task;
import ubersuper.utils.storage.ClientStorage;
import ubersuper.utils.storage.DataStorage;
import ubersuper.utils.ui.Ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Mutable list of {@link Client} items plus high-level operations used by the UI.
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
public class ClientList extends ArrayList<Client> {
    private final ClientStorage clientStorage;

    public ClientList(ClientStorage clientStorage) {
        this.clientStorage = clientStorage;
    }

    /**
     * Stores the current list and prints the "added" confirmation for the provided task.
     *
     * @param c the Client that was just added
     * @return String message
     */
    public String save(Client c) {
        assert c != null : "Client passed to save() must not be null";
        String message = "";
        clientStorage.save(this);
        message += String.format("You now have %d clients in the list \n", this.size());
        message = Ui.printLine() + "Got it! I've added this client:\n" + c + "\n" + message + Ui.printLine();
        return message;
    }

    /**
     * Returns a String of all tasks with their 1-based indices.
     */
    public String list() {
        String clients = IntStream.range(0, this.size())
                .mapToObj(i -> (i + 1) + ". " + this.get(i))
                .collect(Collectors.joining("\n"));

        return "Here are the clients in your list:\n"
                + clients + "\n";
    }

    /**
     * Deletes the i-th client (1-based index), saves the list, and prints a confirmation.
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
            Client t = this.get(i - 1);
            this.remove(i - 1);
            clientStorage.save(this);
            message += String.format("You now have %d clients in the list \n", this.size());
            message = Ui.printLine()
                    + "Ok, I've removed this client from the list: \n"
                    + t + "\n"
                    + message
                    + Ui.printLine();
        } catch (UberExceptions e) {
            return Ui.printLine() + e.getMessage() + "\n" + Ui.printLine();
        }
        return message;
    }


    /**
     * Finds and prints clients whose name contain any of the given keywords (case-insensitive).
     * Usage: {@code find <keyword(s)>}
     * Examples:
     * <pre>
     *   find John
     *   find Sean Ow
     * </pre>
     *
     * @return String message
     * Matching is OR across keywords: a task is listed if its description contains at least one keyword.
     */
    public String find(String input) {
        String[] parts = input.split("\\s+", 2);
        if (parts.length < 2 || parts[1].isBlank()) {
            throw new UberExceptions("Use: find <keyword(s)>");
        }

        // Split the query into keywords and match, case-insensitive
        String[] keywords = parts[1].toLowerCase().split("\\s+");

        String matches = IntStream.range(0, this.size())
                .mapToObj(i -> {
                    Client t = this.get(i);
                    assert t != null : "Task in TaskList should not be null";
                    String name = t.getName().toLowerCase();
                    boolean found = Arrays.stream(keywords)
                            .filter(k -> !k.isBlank())
                            .anyMatch(name::contains);
                    return found ? (i + 1) + ". " + t : null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.joining("\n"));

        if (matches.isBlank()) {
            matches = "(No matches.)";
        }
        return "Here are the matching clients in your list: \n"
                + matches;
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
        clientStorage.save(this);
        message += String.format("You now have %d clients in the list \n", this.size());
        message = Ui.printLine() + "Got it! I've added this task:\n" + t + "\n" + message + Ui.printLine();
        return message;
    }
}
