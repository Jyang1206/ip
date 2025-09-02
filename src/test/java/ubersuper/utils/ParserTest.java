package ubersuper.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ubersuper.exceptions.UberExceptions;
import ubersuper.utils.command.CommandType;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class ParserTest {

    @Test
    void fromInput_matchesHeadTokenCaseInsensitive_returnsCorrectKeyword() {
        assertEquals(CommandType.BYE, Parser.fromInput("bye"));
        assertEquals(CommandType.BYE, Parser.fromInput("Bye"));
        assertEquals(CommandType.LIST, Parser.fromInput("list   "));
    }
    
    @Test
    void fromInput_withNonExactPrefix_returnsUNKNOWN() {
        // Should NOT treat "listall" as "list"
        assertEquals(CommandType.UNKNOWN, Parser.fromInput("listall"));
    }

    @Test
    void fromInput_withOnDateToken_returnsONDATE() {
        // Assumes ONDATE keyword is "ondate" and matching is case-insensitive
        assertEquals(CommandType.ONDATE, Parser.fromInput("ondate 2019-12-02"));
        assertEquals(CommandType.ONDATE, Parser.fromInput("OnDaTe 2019-12-02"));
    }
    @Test
    @DisplayName("ISO date only -> midnight")
    void parseWhen_withIsoDate_returnsStartOfDay() throws UberExceptions {
        LocalDateTime dt = Parser.parseWhen("2019-12-02");
        assertEquals(LocalDate.of(2019, 12, 2).atStartOfDay(), dt);
    }

    @Test
    @DisplayName("ISO date-time with 'T'")
    void parseWhen_withIsoDateTimeWithT_returnsExactTime() throws UberExceptions {
        LocalDateTime dt = Parser.parseWhen("2019-12-02T18:00");
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), dt);
    }

    @Test
    @DisplayName("ISO date-time with space")
    void parseWhen_withIsoDateTimeWithSpace_returnsExactTime() throws UberExceptions {
        LocalDateTime dt = Parser.parseWhen("2019-12-02 18:00");
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), dt);
    }

    @Test
    @DisplayName("d/M/uuuu HHmm and d/M/uuuu")
    void parseWhen_withSlashDateAndHHmm_returnsParsed() throws UberExceptions {
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0),
                Parser.parseWhen("2/12/2019 1800"));
        assertEquals(LocalDate.of(2019, 12, 2).atStartOfDay(),
                Parser.parseWhen("2/12/2019"));
    }

    @Test
    @DisplayName("d-M-uuuu HHmm and d-M-uuuu")
    void parseWhen_withDashDateAndHHmm_returnsParsed() throws UberExceptions {
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0),
                Parser.parseWhen("2-12-2019 1800"));
        assertEquals(LocalDate.of(2019, 12, 2).atStartOfDay(),
                Parser.parseWhen("2-12-2019"));
    }

    @Test
    @DisplayName("Rejects invalid inputs")
    void parseWhen_invalidInputs_throwsUberExceptionsid_inputs() {
        assertThrows(UberExceptions.class, () -> Parser.parseWhen("not-a-date"));
        assertThrows(UberExceptions.class, () -> Parser.parseWhen("10-10-20"));  // 2-digit year not supported
        assertThrows(UberExceptions.class, () -> Parser.parseWhen("2019/12/02")); // unsupported pattern
    }

}
