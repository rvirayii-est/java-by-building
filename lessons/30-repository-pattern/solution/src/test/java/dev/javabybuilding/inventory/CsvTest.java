package dev.javabybuilding.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;

class CsvTest {

    @Test
    void aPlainFieldIsNotChanged() {
        assertEquals("Rice 25kg", Csv.escape("Rice 25kg"));
    }

    @Test
    void aFieldWithACommaIsWrappedInQuotes() {
        assertEquals("\"Rice, premium 5kg\"", Csv.escape("Rice, premium 5kg"));
    }

    @Test
    void quotesInsideAFieldAreDoubled() {
        assertEquals("\"say \"\"hi\"\"\"", Csv.escape("say \"hi\""));
    }

    @Test
    void joinEscapesEachFieldAndPutsCommasBetween() {
        assertEquals("\"Rice, 5kg\",12,", Csv.join(List.of("Rice, 5kg", "12", "")));
    }

    @Test
    void parseLineSplitsOnCommas() {
        assertEquals(List.of("Rice 25kg", "125050", "10"), Csv.parseLine("Rice 25kg,125050,10"));
    }

    @Test
    void parseLineKeepsEmptyFields() {
        assertEquals(List.of("a", "", "c", ""), Csv.parseLine("a,,c,"));
        assertEquals(List.of(""), Csv.parseLine(""));
    }

    @Test
    void parseLineUnderstandsQuotedFields() {
        assertEquals(List.of("Rice, premium 5kg", "12"), Csv.parseLine("\"Rice, premium 5kg\",12"));
        assertEquals(List.of("say \"hi\""), Csv.parseLine("\"say \"\"hi\"\"\""));
    }

    @Test
    void whatJoinWritesParseLineReadsBack() {
        List<String> fields = List.of("plain", "with, comma", "with \"quote\"", "", "both, \"of\" them");
        assertEquals(fields, Csv.parseLine(Csv.join(fields)));
    }

    @Test
    void aQuoteThatIsNeverClosedIsADataProblem() {
        DataFileException e = assertThrows(DataFileException.class, () -> Csv.parseLine("\"Rice,12"));
        assertEquals("A quote was opened but never closed: \"Rice,12", e.getMessage());
    }
}
