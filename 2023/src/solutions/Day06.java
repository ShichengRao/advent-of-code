package src.solutions;

import src.meta.DayTemplate;
import src.meta.Utils;

import java.util.Scanner;

public class Day06 extends DayTemplate {

    @Override
    public String[] fullSolve(Scanner in) {
        long answer1 = 1;
        long answer2 = 1;
        String timeLine = in.nextLine();
        String distLine = in.nextLine();
        long[] times1 = Utils.longs(timeLine);
        long[] dists1 = Utils.longs(distLine);
        long[] times2 = Utils.longs(timeLine.replace(" ", ""));
        long[] dists2 = Utils.longs(distLine.replace(" ", ""));
        for (int i = 0; i < times1.length; i++) {
            long time = times1[i];
            long dist = dists1[i];
            long threshold = (long) Math.ceil((time -Math.sqrt(time * time - 4.0 * dist)) / 2);
            answer1 *= time - threshold * 2 + 1;
        }
        for (int i = 0; i < times2.length; i++) {
            long time = times2[i];
            long dist = dists2[i];
            long threshold = (long) Math.ceil((time -Math.sqrt(time * time - 4.0 * dist)) / 2);
            answer2 *= time - threshold * 2 + 1;
        }
        return new String[]{answer1 + "", answer2 + ""};
    }

    /**
     * Main solving method.
     *
     * @param part1 The solver will solve part 1 if param is set to true.
     *              The solver will solve part 2 if param is set to false.
     * @param in    The solver will read data from this Scanner.
     * @return Returns answer in string format.
     */
    public String solve(boolean part1, Scanner in) {
        long answer = 1;
        String timeLine = part1 ? in.nextLine() : in.nextLine().replace(" ", "");
        String distLine = part1 ? in.nextLine() : in.nextLine().replace(" ", "");
        long[] times = Utils.longs(timeLine);
        long[] dists = Utils.longs(distLine);
        for (int i = 0; i < times.length; i++) {
            long time = times[i];
            long dist = dists[i];
            long threshold = (long) Math.ceil((time -Math.sqrt(time * time - 4.0 * dist)) / 2);
            answer *= time - threshold * 2 + 1;
        }
        return answer + "";
    }
}