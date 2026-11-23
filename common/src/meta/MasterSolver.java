package src.meta;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Runs the days of one year. Run it from that year's folder, with that year's classes on the
 * classpath; each year has an IntelliJ run configuration ("2026 MasterSolver", ...) set up that way.
 * <p>
 * Arguments: {@code [day ...] [--part=1|2] [--time] [--full] [--check] [--golf]}
 * <ul>
 *     <li>{@code day ...}: days to solve, printing both answers (or only the part given)</li>
 *     <li>{@code --time}: then time every day that has an input, skipping days that exclude themselves</li>
 *     <li>{@code --full}: with --time, time whole days through fullSolve() instead of part by part</li>
 *     <li>{@code --check}: compare every day against data/expectedResults.txt</li>
 *     <li>{@code --golf}: use the golfed classes (A, B, ...) instead of DayNN</li>
 * </ul>
 */
public class MasterSolver {

    private static final String DATA_DAY = "./data/day";
    private static final String SOLUTIONS_DAY = "src.solutions.Day";
    private static final String[] GOLFED_DAYS = "A B C D E F G H I J K L M N O P Q R S T U V W X Y".split(" ");
    private static final String PART = " part ";
    private static final int MAX_DAYS = 25;
    private static boolean useGolfed;

    public static void main(String[] args) throws Exception {
        List<Integer> days = new ArrayList<>();
        boolean[] parts = {true, false};
        boolean runTimer = false;
        boolean timeFullDays = false;
        boolean correctnessCheck = false;
        for (String arg : args) {
            switch (arg) {
                case "--time" -> runTimer = true;
                case "--full" -> timeFullDays = true;
                case "--check" -> correctnessCheck = true;
                case "--golf" -> useGolfed = true;
                case "--part=1" -> parts = new boolean[]{true};
                case "--part=2" -> parts = new boolean[]{false};
                default -> {
                    if (!arg.matches("\\d{1,2}")) {
                        usage("unknown argument: " + arg);
                        return;
                    }
                    days.add(Integer.parseInt(arg));
                }
            }
        }
        if (days.isEmpty() && !runTimer && !correctnessCheck) {
            usage("nothing to do");
            return;
        }

        for (int day : days) {
            String zeroFilledDay = zeroFill(day);
            Class<?> cls = dayClass(day);
            File file = inputFile(day);
            if (cls == null || !file.exists()) {
                System.out.println("Day " + zeroFilledDay + ": " + (cls == null ? "no solution class" : "no input at " + file));
                continue;
            }
            for (boolean part1 : parts) {
                String answer = solve(cls, part1, file);
                System.out.println("Day " + zeroFilledDay + PART + (part1 ? 1 : 2) + " solution: " + answer);
            }
        }
        if (runTimer) {
            timer(timeFullDays && !useGolfed);
        }
        if (correctnessCheck) {
            correctnessCheck();
        }
    }

    private static void usage(String problem) {
        System.out.println(problem);
        System.out.println("usage: MasterSolver [day ...] [--part=1|2] [--time] [--full] [--check] [--golf]");
    }

    /**
     * Times every day that has a solution and an input, part by part or (with fullDays) whole days
     * through fullSolve(). Days whose exclude() returns true are skipped.
     */
    public static void timer(boolean fullDays) throws Exception {
        double totalTime = 0.0;
        for (int day = 1; day <= MAX_DAYS; day++) {
            String zeroFilledDay = zeroFill(day);
            Class<?> cls = dayClass(day);
            File file = inputFile(day);
            if (cls == null || !file.exists() || (!useGolfed && newDay(cls).exclude())) {
                continue;
            }
            if (fullDays) {
                double time;
                try (Scanner in = new Scanner(file)) {
                    time = newDay(cls).dayTimer(in);
                }
                System.out.println("Day " + zeroFilledDay + " execution time: " + time);
                totalTime += time;
            } else {
                for (int part = 1; part <= 2; part++) {
                    double time;
                    if (useGolfed) {
                        long start = System.nanoTime();
                        solve(cls, part == 1, file);
                        time = (System.nanoTime() - start) / 1000000.0;
                    } else {
                        try (Scanner in = new Scanner(file)) {
                            time = newDay(cls).timer(part == 1, in);
                        }
                    }
                    System.out.println("Day " + zeroFilledDay + PART + part + " execution time: " + time);
                    totalTime += time;
                }
            }
        }
        System.out.println("Total execution time (ms): " + totalTime);
    }

    /**
     * Compares every day that has a solution and an input against data/expectedResults.txt,
     * which holds part 1 and part 2 of each day on consecutive lines.
     */
    public static boolean correctnessCheck() throws Exception {
        File expected = new File("./data/expectedResults.txt");
        if (!expected.exists()) {
            System.out.println("No data/expectedResults.txt to check against.");
            return false;
        }
        List<String> expectedAnswers = Files.readAllLines(expected.toPath());
        boolean allCorrect = true;
        for (int day = 1; day <= MAX_DAYS; day++) {
            Class<?> cls = dayClass(day);
            File file = inputFile(day);
            if (cls == null || !file.exists()) {
                continue;
            }
            for (int part = 1; part <= 2; part++) {
                int line = (day - 1) * 2 + part - 1;
                if (line >= expectedAnswers.size()) {
                    continue;
                }
                String expectedAnswer = expectedAnswers.get(line);
                String answer = solve(cls, part == 1, file);
                if (!expectedAnswer.equals(answer)) {
                    System.out.println("Day " + zeroFill(day) + PART + part + " solution: " + answer
                            + " doesn't match expected result of " + expectedAnswer);
                    allCorrect = false;
                }
            }
        }
        if (allCorrect) {
            System.out.println("Everything checks out!");
        }
        return allCorrect;
    }

    private static String zeroFill(int day) {
        return (day < 10 ? "0" : "") + day;
    }

    private static File inputFile(int day) {
        return new File(DATA_DAY + zeroFill(day) + ".txt");
    }

    /** The class for a day, or null if this year has no solution for it. */
    private static Class<?> dayClass(int day) {
        try {
            return Class.forName(useGolfed ? GOLFED_DAYS[day - 1] : SOLUTIONS_DAY + zeroFill(day));
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    /** A new instance of a regular (not golfed) day; every DayNN class extends DayTemplate. */
    private static DayTemplate newDay(Class<?> cls) throws ReflectiveOperationException {
        return cls.asSubclass(DayTemplate.class).getDeclaredConstructor().newInstance();
    }

    private static String solve(Class<?> cls, boolean part1, File file) throws Exception {
        if (useGolfed) {
            return solveGolfed(cls, part1, file);
        }
        try (Scanner in = new Scanner(file)) {
            return newDay(cls).solve(part1, in);
        }
    }

    /**
     * Golfed classes (A, B, ...) are package-private, have no package and share no type, since
     * "public" or "extends DayTemplate" would cost characters. Calling s() from src.meta
     * therefore needs reflection and setAccessible.
     */
    private static String solveGolfed(Class<?> cls, boolean part1, File file) throws Exception {
        Constructor<?> ctor = cls.getDeclaredConstructor();
        ctor.setAccessible(true); // NOSONAR golfed classes are package-private on purpose
        Method s = cls.getDeclaredMethod("s", boolean.class, String[].class);
        s.setAccessible(true); // NOSONAR golfed classes are package-private on purpose
        String[] lines = Files.readAllLines(file.toPath()).toArray(String[]::new);
        return String.valueOf(s.invoke(ctor.newInstance(), part1, lines));
    }
}
