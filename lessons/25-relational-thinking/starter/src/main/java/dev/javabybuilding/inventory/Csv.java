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
        if (field.contains(",") || field.contains("\"") || field.contains("\n") || field.contains("\r")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
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
        StringBuilder current = new StringBuilder();
        boolean insideQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (insideQuotes) {
                if (c == '"' && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"'); // two quotes in a row inside quotes means one real quote
                    i++;
                } else if (c == '"') {
                    insideQuotes = false; // the closing quote
                } else {
                    current.append(c);
                }
            } else if (c == '"') {
                insideQuotes = true;
            } else if (c == ',') {
                fields.add(current.toString()); // a comma outside quotes ends the field
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        if (insideQuotes) {
            throw new DataFileException("A quote was opened but never closed: " + line);
        }
        fields.add(current.toString());
        return fields;
    }
}
