package src.solutions;

import src.meta.DayTemplate;
import src.objects.Direction;

import java.util.*;

public class Day18 extends DayTemplate {

    int max;

    int max2;

    @Override
    public String[] fullSolve(Scanner in) {
        List<DoubleInstruction> instructions = parse2(in);
        long[] answer = solve2(instructions);
        return new String[]{answer[0] + "", answer[1] + ""};
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
        List<Instruction> instructions = parse(in, part1);
        long answer = solve(instructions);
        return answer + "";
    }

    private long solve(List<Instruction> instructions){
        long answer = 0;
        List<Long> xVals = new ArrayList<>();
        List<Long> yVals = new ArrayList<>();
        long x = 0;
        long y = 0;
        for (Instruction instruction : instructions) {
            x += (long) instruction.distance * instruction.direction.dx;
            y += (long) instruction.distance * instruction.direction.dy;
            xVals.add(x);
            yVals.add(y);
        }
        for (int i = 0; i < xVals.size(); i++) {
            answer -= xVals.get(i) * yVals.get((i + 1) % xVals.size());
            answer += xVals.get((i + 1) % xVals.size()) * yVals.get(i);
        }
        answer = Math.abs(answer) / 2;
        answer += max / 2 + 1;
        return answer;
    }

    private long[] solve2(List<DoubleInstruction> instructions){
        long answer1 = 0;
        long answer2 = 0;
        List<Long> xVals1 = new ArrayList<>();
        List<Long> yVals1 = new ArrayList<>();
        List<Long> xVals2 = new ArrayList<>();
        List<Long> yVals2 = new ArrayList<>();
        long x1 = 0;
        long y1 = 0;
        long x2 = 0;
        long y2 = 0;
        for (DoubleInstruction instruction : instructions) {
            x1 += (long) instruction.distance1 * instruction.direction1.dx;
            y1 += (long) instruction.distance1 * instruction.direction1.dy;
            x2 += (long) instruction.distance2 * instruction.direction2.dx;
            y2 += (long) instruction.distance2 * instruction.direction2.dy;
            xVals1.add(x1);
            yVals1.add(y1);
            xVals2.add(x2);
            yVals2.add(y2);
        }
        for (int i = 0; i < xVals1.size(); i++) {
            answer1 -= xVals1.get(i) * yVals1.get((i + 1) % xVals1.size());
            answer1 += xVals1.get((i + 1) % xVals1.size()) * yVals1.get(i);
        }
        for (int i = 0; i < xVals2.size(); i++) {
            answer2 -= xVals2.get(i) * yVals2.get((i + 1) % xVals2.size());
            answer2 += xVals2.get((i + 1) % xVals2.size()) * yVals2.get(i);
        }
        answer1 = Math.abs(answer1) / 2;
        answer1 += max / 2 + 1;
        answer2 = Math.abs(answer2) / 2;
        answer2 += max2 / 2 + 1;
        return new long[]{answer1, answer2};
    }

    private List<Instruction> parse(Scanner in, boolean part1){
        List<Instruction> instructions = new ArrayList<>();
        max = 0;
        while (in.hasNext()) {
            String line = in.nextLine();
            instructions.add(new Instruction(line, part1));
            max += instructions.getLast().distance;
        }
        return instructions;
    }

    private List<DoubleInstruction> parse2(Scanner in){
        List<DoubleInstruction> instructions = new ArrayList<>();
        max = 0;
        max2 = 0;
        while (in.hasNext()) {
            String line = in.nextLine();
            instructions.add(new DoubleInstruction(line));
            max += instructions.getLast().distance1;
            max2 += instructions.getLast().distance2;
        }
        return instructions;
    }
}

class Instruction {
    int distance;
    Direction direction;

    public Instruction(String line, boolean part1) {
        if (part1) {
            distance = Integer.parseInt(line.split(" ")[1]);
            direction = Direction.of(line.charAt(0));
        } else {
            String hex = line.split("[#)]")[1];
            direction = Direction.of("RDLU".charAt(Integer.parseInt(hex.substring(hex.length() - 1))));
            distance = Integer.parseInt(hex.substring(0, hex.length() - 1), 16);
        }
    }
}

class DoubleInstruction {
    int distance1;

    Direction direction1;

    int distance2;

    Direction direction2;

    public DoubleInstruction(String line){
        distance1 = Integer.parseInt(line.split(" ")[1]);
        direction1 = Direction.of(line.charAt(0));
        String hex = line.split("[#)]")[1];
        direction2 = Direction.of("RDLU".charAt(Integer.parseInt(hex.substring(hex.length() - 1))));
        distance2 = Integer.parseInt(hex.substring(0, hex.length() - 1), 16);
    }

}