package ubersuper.utils.command;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CommandTypeTest {

    @Test
    void fromInput_matchesHeadTokenCaseInsensitive_returnsCorrectKeyword() {
        assertEquals(CommandType.BYE, CommandType.fromInput("bye"));
        assertEquals(CommandType.BYE, CommandType.fromInput("Bye"));
        assertEquals(CommandType.LIST, CommandType.fromInput("list   "));
    }


    @Test
    void fromInput_withNonExactPrefix_returnsUNKNOWN() {
        // Should NOT treat "listall" as "list"
        assertEquals(CommandType.UNKNOWN, CommandType.fromInput("listall"));
    }

    @Test
    void fromInput_withOnDateToken_returnsONDATE() {
        // Assumes ONDATE keyword is "ondate" and matching is case-insensitive
        assertEquals(CommandType.ONDATE, CommandType.fromInput("ondate 2019-12-02"));
        assertEquals(CommandType.ONDATE, CommandType.fromInput("OnDaTe 2019-12-02"));
    }
}
