package dev.javabybuilding.exercises;

import java.util.regex.Pattern;

/**
 * Lesson 23, exercise 2: branch names. A good branch name says what kind of work it is and what it is about:
 * feature/add-reorder-report, fix/sale-total-sign, docs/update-setup. Only small letters, digits and single hyphens.
 */
public class BranchNames {

    private static final Pattern VALID = Pattern.compile("^(feature|fix|docs|chore)/[a-z0-9]+(-[a-z0-9]+)*$");

    public static boolean isValid(String name) {
        return VALID.matcher(name).matches();
    }

    /** A feature branch for a title: "Add reorder report!" gives "feature/add-reorder-report". */
    public static String fromTitle(String title) {
        return fromTitle("feature", title);
    }

    /**
     * A branch name of the given kind (feature, fix, docs or chore) for a title. Everything that is not a letter or digit
     * becomes a hyphen; repeated hyphens collapse to one; hyphens at the ends are dropped; capitals become small.
     *
     * @throws IllegalArgumentException when the title has no letters or digits, or the kind is not one of the four
     */
    public static String fromTitle(String kind, String title) {
        String slug = title.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("^-+|-+$", "");
        if (slug.isEmpty()) {
            throw new IllegalArgumentException("The title needs at least one letter or digit.");
        }
        String name = kind + "/" + slug;
        if (!isValid(name)) {
            throw new IllegalArgumentException("Unknown kind of branch: " + kind);
        }
        return name;
    }
}
