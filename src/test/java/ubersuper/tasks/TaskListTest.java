package ubersuper.tasks;

import org.junit.jupiter.api.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

public class TaskListTest {

    static class TL extends TaskList {
        TL() {
            super(null);
        }
    }

    private TaskList tasks;
    private PrintStream oldOut;
    private ByteArrayOutputStream out;
    private Locale oldLocale;

    @BeforeEach
    void setup() {
        tasks = new TL();
        oldOut = System.out;
        out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        oldLocale = Locale.getDefault();
        Locale.setDefault(Locale.ENGLISH);
    }

    @AfterEach
    void teardown() {
        System.setOut(oldOut);
        Locale.setDefault(oldLocale);
        tasks.clear();
    }

    @Test
    void list_withTasks_printsHeaderAndNumberedItems() {
        tasks.add(new Todo("T1 do something"));
        tasks.add(new Deadline("D1 report", LocalDateTime.of(2019, 10, 15, 0, 0)));
        tasks.add(new Event("E1 meet", LocalDateTime.of(2019, 12, 2, 9, 0),
                LocalDateTime.of(2019, 12, 2, 10, 0)));

        tasks.list();
        String s = out.toString();

        assertTrue(s.contains("Here are the tasks in your list:"), s);
        assertTrue(s.contains("1. "), s);
        assertTrue(s.contains("2. "), s);
        assertTrue(s.contains("3. "), s);

        assertTrue(s.contains("T1 do something"), s);
        assertTrue(s.contains("D1 report"), s);
        assertTrue(s.contains("E1 meet"), s);
    }

    @Test
    void onDate_withDeadlinesAndEvents_filtersItemsTouchingDay() {
        // Deadlines
        tasks.add(new Deadline("D1", LocalDateTime.of(2019, 12, 2, 0, 0)));  // ✓
        tasks.add(new Deadline("D2", LocalDateTime.of(2019, 12, 3, 0, 0)));  // ✗

        // Events
        tasks.add(new Event("E1",  // ✓ within the day
                LocalDateTime.of(2019, 12, 2, 9, 0),
                LocalDateTime.of(2019, 12, 2, 10, 0)));

        tasks.add(new Event("E2",  // ✓ spans across the day
                LocalDateTime.of(2019, 12, 1, 23, 0),
                LocalDateTime.of(2019, 12, 3,  1, 0)));

        tasks.add(new Event("E3",  // ✗ outside
                LocalDateTime.of(2019, 12, 4,  9, 0),
                LocalDateTime.of(2019, 12, 4, 10, 0)));

        tasks.onDate("ondate 2019-12-02");
        String output = out.toString();

        // Should list D1, E1, E2
        assertTrue(output.contains("D1"), output);
        assertTrue(output.contains("E1"), output);
        assertTrue(output.contains("E2"), output);

        // Should NOT list D2, E3
        assertFalse(output.contains("D2"), output);
        assertFalse(output.contains("E3"), output);

        // Numbering should start at 1 and increment
        assertTrue(output.contains("1."), output);
        assertTrue(output.contains("2."), output);
        assertTrue(output.contains("3."), output);
    }


    private String runOnDate(String arg) {
        out.reset();
        tasks.onDate("ondate " + arg);
        return out.toString();
    }

    @Test
    void onDate_withIsoSlashDashAndDateTime_acceptsInput() {
        String iso = runOnDate("2019-12-02");
        assertTrue(iso.contains("Items on Dec 02 2019:"), iso);

        String slash = runOnDate("2/12/2019");
        assertTrue(slash.contains("Items on Dec 02 2019:"), slash);

        String dash = runOnDate("2-12-2019");
        // If your onDate currently doesn't accept d-M-uuuu, update it to use Parser.parseWhen(...)
        assertTrue(dash.contains("Items on Dec 02 2019:"), dash);

        String isoT = runOnDate("2019-12-02T18:00");
        assertTrue(isoT.contains("Items on Dec 02 2019:"), isoT);

        String isoSpace = runOnDate("2019-12-02 18:00");
        assertTrue(isoSpace.contains("Items on Dec 02 2019:"), isoSpace);
    }

    @Test
    void onDate_withTwoDigitYear_showsUsageHint() {
        String bad = runOnDate("10-10-20");
        assertTrue(bad.contains("Use: onDate <yyyy-mm-dd | dd/MM/yyyy>"), bad);
    }
}
