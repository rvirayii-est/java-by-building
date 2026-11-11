// Run with:  java MethodsAndArrays.java
public class MethodsAndArrays {

    // A method: a named block of code. It takes inputs (parameters) and gives back one result.
    static int total(int[] numbers) {
        int sum = 0;
        for (int number : numbers) {
            sum += number;
        }
        return sum;
    }

    // Returns -1 when the name is not found.
    static int indexOf(String[] names, String name) {
        for (int i = 0; i < names.length; i++) {
            if (names[i].equals(name)) {
                return i;
            }
        }
        return -1;
    }

    // "void" means: do something, give nothing back. The array you pass in is the SAME array, so it changes.
    static void addToAll(int[] numbers, int amount) {
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] += amount;
        }
    }

    // An int is COPIED when passed in, so the caller's variable does not change.
    static void tryToChange(int number) {
        number = 999;
    }

    public static void main(String[] args) {
        int[] quantities = {12, 4, 0, 60};
        String[] names = {"Rice", "Oil", "Sugar", "Sardines"};

        System.out.println("Total units: " + total(quantities));
        System.out.println("Index of Sugar: " + indexOf(names, "Sugar"));
        System.out.println("Index of Salt: " + indexOf(names, "Salt"));

        addToAll(quantities, 10);
        System.out.println("After addToAll: " + quantities[0] + ", " + quantities[1] + ", " + quantities[2] + ", " + quantities[3]);

        int single = 5;
        tryToChange(single);
        System.out.println("single is still " + single);

        int[] copy = new int[quantities.length]; // new arrays start filled with 0
        System.out.println("A new array has length " + copy.length + " and copy[0] is " + copy[0]);
    }
}
