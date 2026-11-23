package src.solutions;

import src.meta.DayTemplate;
import src.meta.Utils;
import src.objects.Coordinate;

import java.util.List;
import java.util.Scanner;

public class Day21 extends DayTemplate {

    int[][] grid;

    /**
     * Main solving method.
     *
     * @param part1 The solver will solve part 1 if param is set to true.
     *              The solver will solve part 2 if param is set to false.
     * @param in    The solver will read data from this Scanner.
     * @return Returns answer in string format.
     */
    public String solve(boolean part1, Scanner in) {
        long answer = 0;
        List<String> lines = Utils.readLines(in);
        grid = Utils.buildGrid(lines, c -> c == '#' ? -1 : 9999);
        Coordinate start = Utils.find(lines, 'S');
        int[][] distances = Utils.distances(grid, start, value -> value != -1);
        int limit = part1 ? 64 : grid.length;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (distances[i][j] != -1 && distances[i][j] <= limit) {
                    grid[i][j] = distances[i][j];
                }
            }
        }
        if (part1) {
            answer = solvePart1();
        }
        if (!part1) {
            answer = solvePart2();
        }
        return answer + "";
    }

    private long solvePart1(){
        long answer = 0;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] % 2 == 0 && grid[i][j] <= 64) {
                    answer++;
                }
            }
        }
        return answer;
    }

    private long solvePart2(){
        long[] evenOddLarge = new long[4];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] != -1 && grid[i][j] != 9999) {
                    evenOddLarge[(i + j) % 2]++;
                    if(grid[i][j] > grid.length / 2){
                        evenOddLarge[(i + j) % 2 + 2]++;
                    }
                }
            }
        }
        long size = 26501365 / grid.length;
        return ((size + 1) * (size + 1) * evenOddLarge[1]) + (size * size * evenOddLarge[0]) - ((size + 1) * evenOddLarge[3]) + (size * evenOddLarge[2]);
    }
}
