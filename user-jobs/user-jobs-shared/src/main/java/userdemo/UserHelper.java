package userdemo;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class UserHelper {
    private static final Set<String> names =
            new HashSet<>(Arrays.asList("James", "John", "Robert", "Michael", "William", "David", "Max", "Bob",
            "Richard", "Joseph", "Thomas", "Charles", "Christopher", "Daniel", "Matthew", "Anthony", "Donald", "Mark",
            "Paul", "Steven", "Andrew", "Kenneth", "Joshua", "Kevin", "Brian", "George", "Edward", "Ronald", "Timothy",
            "Jason", "Jeffrey", "Ryan", "Jacob", "Gary", "Nicholas", "Eric", "Jonathan", "Stephen", "Larry", "Justin",
            "Scott", "Brandon", "Benjamin", "Samuel", "Frank", "Gregory", "Alex", "Patrick", "Jack", "Dennis",
            "Jerry", "Mary", "Patricia", "Jennifer", "Linda", "Olivia", "Barbara", "Susan", "Jessica", "Sarah",
            "Isabella", "Nancy", "Lisa", "Sophia", "Sandra", "Emma", "Emily", "Emily", "Charlotte", "Amelia",
            "Grace", "Ella", "Evelyn", "Natalie", "Jacob", "Michael", "Ricardo", "Daniel", "Lucas", "Joshua",
            "David", "Isaac", "Oliver", "Sebastian", "Aaron", "Thomas", "Nicholas", "Steven", "Robert", "Brandon",
            "Justin", "Alice", "Vincent"));

    private static final List<String> namesList = new ArrayList<>(names);

    private static final Random random = new Random();

    public static String generate() {
        return generate(names.size());
    }

    public static String generate(int sampleSize) {
        if (sampleSize <= 0) {
            throw new IllegalArgumentException(MessageFormat.format(
                    "sampleSize ({0}) must be larger than 0", sampleSize));
        }
        if (names.size() < sampleSize) {
            throw new IllegalArgumentException(MessageFormat.format(
                    "sampleSize ({0}) is larger than names list size ({1})", sampleSize, names.size()));
        }
        List<String> subList = namesList.subList(0, sampleSize);
        int index = random.nextInt(sampleSize);
        return subList.get(index);
    }

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            System.out.println(generate());
        }
    }
}
