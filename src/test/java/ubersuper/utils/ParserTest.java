package ubersuper.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ubersuper.exceptions.UberExceptions;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class ParserTest {

    @Test
    @DisplayName("ISO date only -> midnight")
    void parseWhen_isoDate_only() throws UberExceptions {
        LocalDateTime dt = Parser.parseWhen("2019-12-02");
        assertEquals(LocalDate.of(2019, 12, 2).atStartOfDay(), dt);
    }

    @Test
    @DisplayName("ISO date-time with 'T'")
    void parseWhen_isoDateTime_T() throws UberExceptions {
        LocalDateTime dt = Parser.parseWhen("2019-12-02T18:00");
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), dt);
    }

    @Test
    @DisplayName("ISO date-time with space")
    void parseWhen_isoDateTime_space() throws UberExceptions {
        LocalDateTime dt = Parser.parseWhen("2019-12-02 18:00");
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), dt);
    }

    @Test
    @DisplayName("d/M/uuuu HHmm and d/M/uuuu")
    void parseWhen_slash_formats() throws UberExceptions {
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0),
                Parser.parseWhen("2/12/2019 1800"));
        assertEquals(LocalDate.of(2019, 12, 2).atStartOfDay(),
                Parser.parseWhen("2/12/2019"));
    }

    @Test
    @DisplayName("d-M-uuuu HHmm and d-M-uuuu")
    void parseWhen_dash_formats() throws UberExceptions {
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0),
                Parser.parseWhen("2-12-2019 1800"));
        assertEquals(LocalDate.of(2019, 12, 2).atStartOfDay(),
                Parser.parseWhen("2-12-2019"));
    }

    @Test
    @DisplayName("Rejects invalid inputs")
    void parseWhen_invalid_inputs() {
        assertThrows(UberExceptions.class, () -> Parser.parseWhen("not-a-date"));
        assertThrows(UberExceptions.class, () -> Parser.parseWhen("10-10-20"));  // 2-digit year not supported
        assertThrows(UberExceptions.class, () -> Parser.parseWhen("2019/12/02")); // unsupported pattern
    }

}
