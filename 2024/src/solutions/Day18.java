package src.solutions;

import src.meta.DayTemplate;
import src.objects.Coordinate;

import java.util.*;

import static src.meta.Utils.*;

public class Day18 extends DayTemplate {

    public String solve(boolean part1, Scanner in) {
        int gridSize = 71;
        int[][] grid = new int[gridSize][gridSize];
        List<Coordinate> lines = new ArrayList<>();
        while(in.hasNext()){
            String[] parts = in.nextLine().split(",");
            lines.add(new Coordinate(Integer.parseInt(parts[0]), Integer.parseInt(parts[1])));
        }
        if(part1){
            return bfs(1024, lines, grid) + "";

        }
        else{
            int high = lines.size() - 1;
            int low = 0;
            while(low < high){
                grid = new int[gridSize][gridSize];
                int med = (low + high)/2;
                if(bfs(med, lines, grid) == -1){
                    high = med;
                }
                else{
                    low = med + 1;
                }
            }
            return lines.get(low - 1).x + "," + lines.get(low - 1).y;
        }
    }

    private int bfs(int limit, List<Coordinate> memoryBlock, int[][] grid){
        for(int i = 0; i < limit; i++){
            grid[memoryBlock.get(i).x][memoryBlock.get(i).y] = 1;
        }
        int[][] distance = distances(grid, new Coordinate(0,0), value -> value != 1);
        return distance[grid.length - 1][grid.length - 1];
    }
}