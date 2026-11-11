package dev.javabybuilding.inventory;

/**
 * A data file exists but its contents are wrong: a missing column, a word where a number should be, an unknown
 * discount code. It says which file and which line, so whoever edited the file by hand can fix it.
 * (A file that cannot be opened at all is a different problem: Java reports that with the checked IOException.)
 */
public class DataFileException extends ShopException {

    public DataFileException(String fileName, int lineNumber, String problem) {
        super(fileName + ", line " + lineNumber + ": " + problem);
    }

    public DataFileException(String message) {
        super(message);
    }
}
