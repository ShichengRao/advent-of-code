package src.solutions;
import src.meta.DayTemplate;

import java.util.*;

import static src.meta.Utils.*;

public class Day13 extends DayTemplate {

    public String solve(boolean part1, Scanner in) {
        long answer = 0;
        for (List<String> machine : readSections(in)) {
            answer+=helper(machine, part1);
        }
        return answer+"";
    }

    private long oneCycle(long x1, long y1, long x2, long y2, long x3, long y3){
        if(y3%gcd(y1,y2) != 0 || x3%gcd(x1,x2) !=0){
            return 0;
        }
        long a = 0;
        long b = 0;
        if(y2 * x1 == x2 * y1){
            if(y3%gcd(y1,y2) == 0 && x3%gcd(x1,x2) == 0){
                long xmin = Math.min(x1,x2);
                long ymin = Math.min(y1,y2);
                if(y3/ymin == x3/xmin){
                    if(y1 > 3 * y2){
                        a = y3/y1;
                        b = (y3 - y1 * a)/y2;
                    }
                    else{
                        b = y3/y2;
                        a = (y3 - y2 * b)/y1;
                    }
                }
            }
        }
        else{
            long d = Math.abs(det(x1, x2, y1, y2));
            long d1 = Math.abs(det(x2, x3, y2, y3));
            long d2 = Math.abs(det(x1, x3, y1, y3));
            if(d1 % d != 0 || d2 % d != 0){
                return 0;
            }
            else{
                a = d1 / d;
                b = d2 / d;

            }
        }
        return a * 3 + b;
    }

    private long helper(List<String> lines, boolean part1){
        long[] first = longs(lines.get(0));
        long[] second = longs(lines.get(1));
        long[] third = longs(lines.get(2));
        long x3 = 10000000000000L + third[0];
        long y3 = 10000000000000L + third[1];
        if(part1){
            x3 = third[0];
            y3 = third[1];
        }
        return oneCycle(first[0], first[1], second[0], second[1], x3, y3);
    }
}