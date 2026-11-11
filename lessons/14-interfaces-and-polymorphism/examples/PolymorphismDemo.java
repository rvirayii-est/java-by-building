// Run with:  java PolymorphismDemo.java
import java.util.List;

public class PolymorphismDemo {

    public static void main(String[] args) {
        // One list, three different kinds of object, all promising "greet".
        List<Greeter> greeters = List.of(new Formal(), new Casual(), new Taglish());

        for (Greeter greeter : greeters) {
            System.out.println(greeter.greet("Ana"));   // each object runs ITS OWN greet
        }

        // A method written once, works for every Greeter (even ones written later).
        System.out.println(longest(greeters, "Ana"));

        Greeter anonymous = name -> "Hi " + name + "!";   // an interface with ONE method can be written as a lambda
        System.out.println(anonymous.greet("Ben"));
    }

    static String longest(List<Greeter> greeters, String name) {
        String longest = "";
        for (Greeter greeter : greeters) {
            String text = greeter.greet(name);
            if (text.length() > longest.length()) {
                longest = text;
            }
        }
        return longest;
    }
}

interface Greeter {
    String greet(String name);
}

class Formal implements Greeter {
    public String greet(String name) {
        return "Good day, " + name + ".";
    }
}

class Casual implements Greeter {
    public String greet(String name) {
        return "Hey " + name;
    }
}

class Taglish implements Greeter {
    public String greet(String name) {
        return "Kumusta, " + name + "!";
    }
}
