package ubersuper.utils;

import ubersuper.clients.Client;
import ubersuper.exceptions.UberExceptions;
import ubersuper.utils.command.CommandType;
import ubersuper.utils.ui.Ui;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Parsing user input is done via {@link #fromInput(String)}, which:
 * <ul>
 *   <li>is case-insensitive (e.g., {@code "Bye"}, {@code "BYE"} → {@link #BYE}),</li>
 *   <li>matches the <em>first whitespace-delimited token</em> only,</li>
 *   <li>requires an exact token match (no prefix matching; e.g., {@code "listall"} → {@link #UNKNOWN}),</li>
 *   <li>is null/blank-safe (null or blank input → {@link #UNKNOWN}).</li>
 * </ul>
 * Utility for parsing user-supplied date/time strings into {@link LocalDateTime}.
 * <p>
 * The parser is intentionally permissive and accepts several common formats.
 * If only a date is given (no time component), the returned {@link LocalDateTime}
 * is set to {@code 00:00} (start of day).
 *
 * <h2>Accepted input formats</h2>
 * <ul>
 *   <li><b>ISO date-time</b>: {@code yyyy-MM-dd'T'HH:mm}
 *   <li><b>ISO date-time (space)</b>: {@code yyyy-MM-dd HH:mm}
 *   <li><b>ISO date (date only)</b>: {@code yyyy-MM-dd} → time defaults to {@code 00:00}
 *   <li><b>Slash with compact time</b>: {@code d/M/uuuu HHmm}
 *   <li><b>Slash (date only)</b>: {@code d/M/uuuu} → time defaults to {@code 00:00}
 *   <li><b>Dash with compact time</b>: {@code d-M-uuuu HHmm}
 *   <li><b>Dash (date only)</b>: {@code d-M-uuuu} → time defaults to {@code 00:00}
 * </ul>
 *
 * <p>If the input does not match any of the above, an {@link UberExceptions}
 * is thrown with a helpful message.</p>
 */
public class Parser {

    /**
     * Parses the user's input into a {@link CommandType}.
     * <p><strong>Rules:</strong></p>
     * <ul>
     *   <li>Extracts the first whitespace-delimited token and lower-cases it.</li>
     *   <li>Performs exact token match against known commands (no prefix/substring match).</li>
     *   <li>Returns CommandType.UNKNOWN if the token does not match any command,
     *   or if input is null/blank.</li>
     * </ul>
     *
     * @param input full user input line
     * @return matching {@link CommandType} or CommandType.UNKNOWN if none
     */
    public static CommandType fromInput(String input) {

        if (input == null || input.isBlank()) {
            return CommandType.UNKNOWN;
        }
        String[] parts = input.strip().split("\\s+", 2);
        String head = parts[0].toLowerCase();

        for (CommandType c : CommandType.values()) {
            if (head.equals(c.getKeyword())) {
                return c;
            }
        }
        return CommandType.UNKNOWN;
    }


    /**
     * Parses a date/time string into a {@link LocalDateTime}.
     * <p>
     * The method tries multiple formats (listed in the class Javadoc) in a sensible order.
     * If only a date is supplied, the time component is set to midnight.
     *
     * @param raw user input containing a date/time
     * @return a {@link LocalDateTime} representing the parsed moment
     * @throws UberExceptions if the input cannot be parsed by any supported format
     */
    public static LocalDateTime parseDateTime(String raw) throws UberExceptions {
        String s = raw.trim();

        // 1) ISO date-time: 2019-12-02T18:00 (or "2019-12-02 18:00")
        try {
            if (s.contains("T")) {
                return LocalDateTime.parse(s);
            }
            if (s.matches("\\d{4}-\\d{2}-\\d{2}\\s+\\d{2}:\\d{2}")) {
                return LocalDateTime.parse(s.replace(' ', 'T'));
            }
        } catch (DateTimeParseException ignore) {
            //ignore
        }

        // 2) ISO date only: 2019-12-02  (treat as 00:00)
        try {
            return LocalDate.parse(s).atStartOfDay();
        } catch (DateTimeParseException ignore) {
            //ignore
        }

        // 3) dd/MM/yyyy HHmm   e.g. 2/12/2019 1800
        try {
            DateTimeFormatter f = DateTimeFormatter.ofPattern("d/M/uuuu HHmm");
            return LocalDateTime.parse(s, f);
        } catch (DateTimeParseException ignore) {
            //ignore
        }

        // 4) dd/MM/yyyy        (00:00)
        try {
            DateTimeFormatter f = DateTimeFormatter.ofPattern("d/M/uuuu");
            return LocalDate.parse(s, f).atStartOfDay();
        } catch (DateTimeParseException ignore) {
            //ignore
        }

        // 5) d-M-uuuu HHmm
        try {
            DateTimeFormatter f = DateTimeFormatter.ofPattern("d-M-uuuu HHmm");
            return LocalDateTime.parse(s, f);
        } catch (DateTimeParseException ignore) {
            //ignore
        }

        // 6) d-M-uuuu (00:00)
        try {
            DateTimeFormatter f = DateTimeFormatter.ofPattern("d-M-uuuu");
            return LocalDate.parse(s, f).atStartOfDay();
        } catch (DateTimeParseException ignore) {
            //ignore
        }

        throw new UberExceptions("I couldn't understand the date/time: \""
                + raw
                + "\".\n" + "Try formats like: 2019-12-02, 2019-12-02 18:00, 2/12/2019 1800.");
    }


    /**
     * Parses a string into a string of client details
     * <p>
     *
     * @param raw user input containing a date/time
     * @return a string representing the client's details
     * @throws UberExceptions if the input cannot be parsed by any supported format
     */
    public static Client parseAddClient(String raw) throws UberExceptions{
            String[] parts = raw.split("/");
            if (parts.length < 2) {
                throw new UberExceptions("Use format: addclient <name> /phone <phone number> /email <email address>");
            } else if (parts.length < 3) {
                throw new UberExceptions("Use format: addclient <name> /phone <phone number> /email <email address>");
            }

            String name = parts[0].replaceFirst("addclient", "");
            String phonePart = parts[1].trim(); // "phone ..."
            String emailPart = parts[2].trim(); // "email ..."
            if (name.isEmpty()) {
                throw new UberExceptions("Please give your client a name");
            }
            //ensure correct formatting
            if (!phonePart.toLowerCase().startsWith("phone") || !emailPart.toLowerCase().startsWith("email")) {
                throw new UberExceptions("Use format: addclient <name> /phone <phone number> /email <email address>");
            }
            String phone = phonePart.replaceFirst("phone", "").trim();
            String email = emailPart.replaceFirst("email", "").trim();

            return new Client(name, phone, email);
    }
}
