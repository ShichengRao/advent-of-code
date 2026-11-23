package src.solutions;

import src.meta.DayTemplate;

import java.util.*;

public class Day02 extends DayTemplate {

    public String[] fullSolve(Scanner in) {
        return null;
    }

    public String solve(boolean part1, Scanner in) {
        String line = in.nextLine();
        IntCodeComputer computer = new IntCodeComputer(line);
        if(part1){
            computer.memory[1] = 12;
            computer.memory[2] = 2;
            computer.run();
            return computer.memory[0] + "";
        }
        else{
            for(int noun = 0; noun < 100; noun++){
                for(int verb = 0; verb < 100; verb++){
                    computer = new IntCodeComputer(line);
                    computer.memory[1] = noun;
                    computer.memory[2] = verb;
                    computer.run();
                    if(computer.memory[0] == 19690720){
                        return (100 * noun + verb) + "";
                    }
                }
            }
            return -1 + "";
        }
    }
}