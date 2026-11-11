package dev.javabybuilding.inventory;

import java.util.ArrayList;
import java.util.List;

/**
 * CSV = "comma-separated values": one record per line, fields separated by commas. A spreadsheet program opens it.
 * The one tricky part: a field that itself contains a comma or a quote must be wrapped in double quotes, and a quote
 * inside it is written twice. So the product "Rice, premium 5kg" is written as  "Rice, premium 5kg"  and
 * the text  say "hi"  is written as  "say ""hi""".
 * shortcut: a field may not contain a line break; spreadsheets allow it, this course's files never need it.
 */
public class Csv {

    /** The field as it must appear in a file: unchanged when it is plain, wrapped in quotes when it must be. */
    public static String escape(String field) {
        // TODO 1: when field contains a comma, a double quote or a line break, return it wrapped in double quotes
        // with every inner quote doubled:  "\"" + field.replace("\"", "\"\"") + "\""   Otherwise return field unchanged.
        return field;
    }

    /** Escapes every field and puts commas between them: ["Rice, 5kg", "12"] gives "Rice, 5kg",12 */
    public static String join(List<String> fields) {
        List<String> escaped = new ArrayList<>();
        for (String field : fields) {
            escaped.add(escape(field));
        }
        return String.join(",", escaped);
    }

    /**
     * The opposite of join: splits one line into its fields, understanding quotes. An empty line gives one empty field.
     *
     * @throws DataFileException when a quote is opened and never closed
     */
    public static List<String> parseLine(String line) {
        List<String> fields = new ArrayList<>();
        // TODO 2: walk the line one character at a time, remembering whether you are inside quotes:
        //   - inside quotes: two quotes in a row mean one real quote; a single quote ends the quoted part; anything else is added.
        //   - outside quotes: a quote starts a quoted part; a comma ends the field (add it to fields, start a new one); anything else is added.
        //   - at the end: if you are still inside quotes, throw new DataFileException("A quote was opened but never closed: " + line);
        //     otherwise add the last field.
        // Read CsvTest first: it lists every case.
        return fields;
    }
}
