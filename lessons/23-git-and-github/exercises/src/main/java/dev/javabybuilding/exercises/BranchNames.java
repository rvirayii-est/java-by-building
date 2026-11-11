package dev.javabybuilding.exercises;

/**
 * Lesson 23, exercise 2: branch names. A good branch name says what kind of work it is and what it is about:
 * feature/add-reorder-report, fix/sale-total-sign, docs/update-setup. Only small letters, digits and single hyphens.
 */
public class BranchNames {

    /**
     * TODO: true for  kind/words  where kind is feature, fix, docs or chore, and words are small letters or digits joined by
     * SINGLE hyphens (no hyphen at either end). Hint: a regular expression, and String.matches or Pattern.matcher(name).matches().
     * Pattern: ^(feature|fix|docs|chore)/[a-z0-9]+(-[a-z0-9]+)*$
     */
    public static boolean isValid(String name) {
        return false;
    }

    /** A feature branch for a title: "Add reorder report!" gives "feature/add-reorder-report". */
    public static String fromTitle(String title) {
        return fromTitle("feature", title);
    }

    /**
     * TODO: a branch name of the given kind for a title. Everything that is not a letter or digit becomes a hyphen; repeated
     * hyphens collapse to one; hyphens at the ends are dropped; capitals become small.
     * Hint: title.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("^-+|-+$", "")
     * Throw new IllegalArgumentException("The title needs at least one letter or digit.") when nothing is left, and
     * new IllegalArgumentException("Unknown kind of branch: " + kind) when the finished name is not valid.
     */
    public static String fromTitle(String kind, String title) {
        return null;
    }
}
