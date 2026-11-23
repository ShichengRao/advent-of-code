package src.solutions;

import src.meta.DayTemplate;
import src.objects.Coordinate;
import src.objects.Direction;

import java.util.*;

import static src.meta.Utils.*;

public class Day06 extends DayTemplate {

    public String solve(boolean part1, Scanner in) {
        long answer = 0;
        List<String> lines = new ArrayList<>();
        while (in.hasNext()) {
            lines.add(in.nextLine());
        }
        GuardLocation start = new GuardLocation(0,0, Direction.UP);
        Direction direction = Direction.UP;
        int[][] grid = buildGrid(lines, c -> switch (c) {
            case '.' -> 1;
            case '#' -> 2;
            default -> 3;
        });

        for(int i = 0; i < grid.length; i++){
            for(int j = 0; j < grid[0].length; j++){
               if(grid[i][j] == 3){
                   grid[i][j] = 1;
                   start = new GuardLocation(i,j, Direction.UP);
               }
            }
        }

        if(part1){
            Set<Coordinate> locations = new HashSet<>();
            locations.add(new Coordinate(start.x(), start.y()));
            GuardLocation guard = start;
            while(safe(guard.x(), guard.y(), grid)){
                int x = guard.x() + direction.dx;
                int y = guard.y() + direction.dy;
                if(!safe(x,y,grid)){
                    return locations.size() + "";
                }
                if(grid[x][y] == 2){
                    direction = direction.turnRight();
                }
                else{
                    locations.add(new Coordinate(x,y));
                    guard = new GuardLocation(x,y, direction);
                }
            }
        }
        else{
            return part2(start, direction, grid);

        }
        return answer + "";
    }

    private boolean loop(GuardLocation start, int[][]gridCopy, Direction direction){
        Set<GuardLocation> visited = new HashSet<>();
        GuardLocation guard = start;
        while(!visited.contains(guard)){
            int x = guard.x() + direction.dx;
            int y = guard.y() + direction.dy;
            if(!safe(x,y,gridCopy)){
                return false;
            }
            if(gridCopy[x][y] == 2){
                direction = direction.turnRight();
            }
            else{
                visited.add(guard);
                guard = new GuardLocation(x,y, direction);
            }
        }
        return true;
    }

    private String part2(GuardLocation start, Direction direction, int[][] grid){
        Set<Coordinate> places = new HashSet<>();
        Set<Coordinate> seen = new HashSet<>();
        GuardLocation guard = start;
        while(true){
            int x = guard.x() + direction.dx;
            int y = guard.y() + direction.dy;
            if(!safe(x,y,grid)){
                break;
            }
            if(!seen.contains(new Coordinate(x,y)) && grid[x][y] == 1){
                grid[x][y] = 2;
                if(loop(guard, grid, direction.turnRight())){
                    places.add(new Coordinate(x, y));
                }
                grid[x][y] = 1;
            }

            if(grid[x][y] == 2){
                direction = direction.turnRight();
            }
            else{
                seen.add(new Coordinate(x,y));
                guard = new GuardLocation(x,y, direction);
            }
        }
        return places.size() + "";
    }
}

record GuardLocation (int x, int y, Direction direction){}