// Run with:  java Strings.java
public class Strings {
    public static void main(String[] args) {
        String name = "  Cooking Oil 1L  ";

        System.out.println("Length with spaces: " + name.length());
        String trimmed = name.trim(); // trim() does NOT change name: Strings never change, it returns a new one
        System.out.println("Trimmed: [" + trimmed + "]");
        System.out.println("Upper: " + trimmed.toUpperCase());
        System.out.println("Lower: " + trimmed.toLowerCase());

        System.out.println("First letter: " + trimmed.charAt(0));
        System.out.println("First 7 characters: " + trimmed.substring(0, 7));
        System.out.println("From position 8: " + trimmed.substring(8));
        System.out.println("Position of \"Oil\": " + trimmed.indexOf("Oil"));
        System.out.println("Contains \"Rice\"? " + trimmed.contains("Rice"));

        String typed = "cooking oil 1l";
        System.out.println("equals: " + typed.equals(trimmed));
        System.out.println("equalsIgnoreCase: " + typed.equalsIgnoreCase(trimmed));

        String[] words = trimmed.split(" ");
        System.out.println("Number of words: " + words.length);
        for (String word : words) {
            System.out.println("  word: " + word);
        }

        // Building text piece by piece: StringBuilder is faster than many + in a loop.
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= 3; i++) {
            sb.append("Item ").append(i);
            if (i < 3) {
                sb.append(", ");
            }
        }
        System.out.println(sb);

        // String.format: %s text, %d whole number, %-10s left-aligned in 10 spaces, %5d right-aligned in 5.
        System.out.println(String.format("[%-10s][%5d]", "Rice", 12));
    }
}
