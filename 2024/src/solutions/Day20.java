package src.solutions;

import src.meta.DayTemplate;
import src.objects.Coordinate;
import java.util.*;
import static src.meta.Utils.*;

public class Day20 extends DayTemplate {

    public String solve(boolean part1, Scanner in) {
        long answer = 0;
        List<String> lines = readLines(in);
        int[][] grid = buildGrid(lines, c -> c == '#' ? 2 : 1);
        Coordinate start = find(lines, 'S');
        Coordinate end = find(lines, 'E');

        int[][] bfsFromEnd = distances(grid, end, value -> value != 2);
        int[][] bfsFromStart = distances(grid, start, value -> value != 2);
        int currentDistance = bfsFromEnd[start.x][start.y];
        for(int i = 0; i < bfsFromEnd.length; i++){
            for(int j = 0; j < bfsFromEnd[0].length; j++){
                if(grid[i][j] != 2){
                    for(int k = 0; k < bfsFromStart.length; k++){
                        for(int l = 0; l < bfsFromStart[0].length; l++){
                            if(grid[k][l] != 2 && (Math.abs(k - i) + Math.abs(l - j)) <= (part1?2:20) && (bfsFromStart[i][j] + bfsFromEnd[k][l] + Math.abs(k - i) + Math.abs(l - j)) <= currentDistance - 100){
                                answer++;
                            }
                        }
                    }
                }
            }
        }
        return answer + "";
    }
}