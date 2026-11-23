package src.solutions;

import src.meta.DayTemplate;
import src.meta.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Day05 extends DayTemplate {

    public String solve(boolean part1, Scanner in) {
        long answer = 0;
        List<List<String>> sections = Utils.readSections(in);
        List<IDRange> ranges = new ArrayList<>();
        for (String l : sections.get(0)) {
            long[] bounds = Utils.longs(l);
            ranges.add(new IDRange(bounds[0], bounds[1]));
        }
        Collections.sort(ranges);
        if(!part1){
            IDRange currRange = ranges.getFirst();
            for(IDRange r: ranges){
                if(r.low > currRange.high){
                    answer += currRange.high - currRange.low + 1;
                    currRange = r;
                }
                else{
                    currRange.high = Math.max(currRange.high, r.high);
                }
            }
            answer += currRange.high - currRange.low + 1;
        }
        else{
            for (String l : sections.get(1)) {
                long line = Long.parseLong(l);
                for(IDRange range: ranges){
                    if(range.contains(line)){
                        answer++;
                        break;
                    }
                }
            }
        }
        return answer+ "";
    }
}

class IDRange implements Comparable<IDRange>{
    long low;
    long high;

    public IDRange(long l, long h){
        low = l;
        high = h;
    }

    public boolean contains(long l){
        return l >= low && l <= high;
    }

    @Override
    public int compareTo(IDRange o) {
        if(low > o.low){
            return 1;
        }
        if(o.low > low){
            return -1;
        }
        return 0;
    }

    public String toString(){
        return low + " " + high;
    }
}