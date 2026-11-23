import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.Scanner;

/**
 * Runs one day's solver. Invoked by ./aoc, which compiles only this file plus the day it
 * runs (and whatever that day references).
 *
 * <pre>java Run &lt;class&gt; &lt;input-file&gt; &lt;parts&gt; [golf]</pre>
 *
 * Regular solvers expose {@code solve(boolean part1, Scanner in)}; golfed ones expose
 * {@code s(boolean part1, String[] lines)}. A fresh instance is used for each part.
 * <p>
 * Run has no package, like the golfed classes, so it can call their package-private s()
 * without setAccessible. Moving Run into a package would break golfed runs.
 */
public class Run {
    public static void main(String[] args) throws Throwable {
        Class<?> cls = Class.forName(args[0]);
        File input = new File(args[1]);
        boolean golf = args.length > 3 && args[3].equals("golf");
        Method method = golf
                ? cls.getDeclaredMethod("s", boolean.class, String[].class)
                : cls.getMethod("solve", boolean.class, Scanner.class);
        Constructor<?> ctor = cls.getDeclaredConstructor();

        for (char part : args[2].toCharArray()) {
            boolean part1 = part == '1';
            Object solver = ctor.newInstance();
            long start = System.nanoTime();
            Object answer;
            try {
                if (golf) {
                    String[] lines = Files.readAllLines(input.toPath()).toArray(String[]::new);
                    answer = method.invoke(solver, part1, lines);
                } else {
                    try (Scanner in = new Scanner(input)) {
                        answer = method.invoke(solver, part1, in);
                    }
                }
            } catch (InvocationTargetException e) {
                throw e.getCause();
            }
            double ms = (System.nanoTime() - start) / 1e6;
            System.out.printf("Part %c: %s  (%.1f ms)%n", part, answer, ms);
        }
    }
}
