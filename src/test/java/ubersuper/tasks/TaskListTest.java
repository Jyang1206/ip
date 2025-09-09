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
    void list_withTasks_returnsHeaderAndNumberedItems() {
        tasks.add(new Todo("T1 do something"));
        tasks.add(new Deadline("D1 report", LocalDateTime.of(2019, 10, 15, 0, 0)));
        tasks.add(new Event("E1 meet", LocalDateTime.of(2019, 12, 2, 9, 0),
                LocalDateTime.of(2019, 12, 2, 10, 0)));

        String output = tasks.list();

        assertTrue(output.contains("Here are the tasks in your list:"), output);
        assertTrue(output.contains("1. "), output);
        assertTrue(output.contains("2. "), output);
        assertTrue(output.contains("3. "), output);

        assertTrue(output.contains("T1 do something"), output);
        assertTrue(output.contains("D1 report"), output);
        assertTrue(output.contains("E1 meet"), output);
    }
    @Test
    void onDate_withDeadlinesAndEvents_returnsItemsWithOriginalIndices() {
        tasks.add(new Todo("T0 dummy")); // index 1
        tasks.add(new Deadline("D1", LocalDateTime.of(2019, 12, 2, 0, 0)));  // index 2 ✓
        tasks.add(new Deadline("D2", LocalDateTime.of(2019, 12, 3, 0, 0)));  // index 3 ✗
        tasks.add(new Event("E1",  // index 4 ✓
                LocalDateTime.of(2019, 12, 2, 9, 0),
                LocalDateTime.of(2019, 12, 2, 10, 0)));
        tasks.add(new Event("E2",  // index 5 ✓
                LocalDateTime.of(2019, 12, 1, 23, 0),
                LocalDateTime.of(2019, 12, 3, 1, 0)));

        String output = tasks.onDate("ondate 2019-12-02");

        // Should match index in actual list 2, 4, 5
        assertTrue(output.contains("2. "), output);
        assertTrue(output.contains("4. "), output);
        assertTrue(output.contains("5. "), output);

        assertFalse(output.contains("3. "), output);
    }

    private String runOnDate(String arg) {
        return tasks.onDate("ondate " + arg);
    }

    @Test
    void onDate_acceptsSupportedDateFormats() {
        String iso = runOnDate("2019-12-02");
        assertTrue(iso.contains("Items on Dec 02 2019:"), iso);

        String slash = runOnDate("2/12/2019");
        assertTrue(slash.contains("Items on Dec 02 2019:"), slash);
    }

    @Test
    void onDate_withUnsupportedFormats_returnsUsageHint() {
        assertTrue(runOnDate("2-12-2019")
                .contains("Use: onDate <yyyy-mm-dd | dd/MM/yyyy>"));

        assertTrue(runOnDate("2019-12-02T18:00")
                .contains("Use: onDate <yyyy-mm-dd | dd/MM/yyyy>"));

        assertTrue(runOnDate("2019-12-02 18:00")
                .contains("Use: onDate <yyyy-mm-dd | dd/MM/yyyy>"));
    }

    @Test
    void onDate_withTwoDigitYear_returnsUsageHint() {
        String bad = runOnDate("10-10-20");
        assertTrue(bad.contains("Use: onDate <yyyy-mm-dd | dd/MM/yyyy>"), bad);
    }
    @Test
    void find_withKeyword_returnsOriginalIndices() {
        tasks.add(new Todo("read book"));  // index 1
        tasks.add(new Deadline("return book", LocalDateTime.of(2019, 10, 15, 0, 0))); // index 2
        tasks.add(new Event("meeting", LocalDateTime.of(2019, 12, 2, 9, 0),
                LocalDateTime.of(2019, 12, 2, 10, 0))); // index 3

        String output = tasks.find("find book");

        assertTrue(output.contains("1. [T]"), output);
        assertTrue(output.contains("2. [D]"), output);
        assertFalse(output.contains("3."), output);
    }

    @Test
    void find_withNoMatches_returnsNoMatchesMessage() {
        tasks.add(new Todo("read book"));

        String output = tasks.find("find hello");

        assertTrue(output.contains("(No matches.)"), output);
    }
}