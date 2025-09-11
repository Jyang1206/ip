package ubersuper.clients;

import ubersuper.tasks.TaskType;

import java.time.LocalDate;

/**
 * Client class
 * <p>
 * Stores the shared state (name, phone, and email) and
 * provides common formatting helpers for printing and persistence.
 * Subclasses must provide:
 * <ul>
 *   <li>{@link #formatString()} — a single-line pipe-separated form for storage</li>
 * </ul>
 */
public class Client {
    private final String name;
    private final String phone;
    private final String email;

    /**
     * Creates a task with the given name and phone and email.
     *
     * @param name identifier of client
     * @param phone contact details of client
     * @param email email address of client
     */
    public Client(String name, String phone, String email) {
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    @Override
    public String toString() {
        return "Client: " + name + "\n"
                + "Phone Number: " + phone + "\n"
                + "Email: " + email + "\n";

    }

    public String formatString() {
        return name + "|" + phone + "|" + email;
    }

    public String getName() {
        return name;
    }
}
