package src.solutions;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Day05 extends src.meta.DayTemplate  {
    public String[] fullSolve(Scanner in) {
        return null;
    }

    public String solve(boolean part1, Scanner in) {
        String line = in.nextLine();
        List<Integer> inputs = new ArrayList<>();
        if(part1){
            inputs.add(1);
        }
        else{
            inputs.add(5);
        }
        IntCodeComputer computer = new IntCodeComputer(line, inputs);
        computer.run();
        return computer.outputs.getLast() + "";
    }
}
