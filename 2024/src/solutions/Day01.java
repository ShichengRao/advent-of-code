package src.solutions;

import src.meta.DayTemplate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class Day01 extends DayTemplate {

    public String solve(boolean part1, Scanner in) {

        long answer = 0;
        List<Integer> left = new ArrayList<>();
        List<Integer> right = new ArrayList<>();
        while (in.hasNext()) {
            String[] line = in.nextLine().split(" {3}");
            left.add(Integer.parseInt(line[0]));
            right.add(Integer.parseInt(line[1]));
        }
        if(part1){
            left.sort(Comparator.naturalOrder());
            right.sort(Comparator.naturalOrder());
            for(int i = 0; i < left.size(); i++){
                answer += Math.abs(left.get(i) - right.get(i));
            }
        }
        else{
            for(Integer l: left){
                for(Integer r: right){
                    if(r.equals(l)){
                        answer+=l;
                    }
                }
            }
        }
        return answer+ "";
    }
}
