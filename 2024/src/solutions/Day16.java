package src.solutions;

import src.meta.DayTemplate;
import src.objects.Coordinate;
import src.objects.Direction;

import java.util.*;

import static src.meta.Utils.*;

public class Day16 extends DayTemplate {

    public String solve(boolean part1, Scanner in) {
        long answer = 0;
        List<String> lines = new ArrayList<>();
        while (in.hasNext()) {
            String line = in.nextLine();
            lines.add(line);
        }
        int[][] grid = buildGrid(lines, c -> c == '#' ? 2 : 1);
        Coordinate reindeer = find(lines, 'S');
        Coordinate exit = find(lines, 'E');

        if(part1){
            answer = bfs(grid, reindeer, exit);
        }
        else{
            int[][] bestSeats = new int[grid.length][grid[0].length];
            String path = bestPath(grid, reindeer, exit);
            mark(bestSeats, path, reindeer);
            List<String> otherBests = findOthers(grid, reindeer, exit, path);
            for(String other: otherBests){
                mark(bestSeats, other, reindeer);
            }
            for(int i = 0; i < bestSeats.length; i++){
                for(int j = 0; j < bestSeats.length; j++){
                    answer+= bestSeats[i][j];
                }
            }
        }

        return answer + "";
    }

    private void mark(int[][] bestSeats, String path, Coordinate reindeer){
        path = path.substring(1);
        bestSeats[reindeer.x][reindeer.y] = 1;
        int deltax = 0;
        int deltay = 0;
        Direction direction = Direction.RIGHT;
        for(char c: path.toCharArray()){
            if(c == 'R'){
                direction = direction.turnRight();
            }
            else{
                deltax += direction.dx;
                deltay += direction.dy;
                bestSeats[reindeer.x + deltax][reindeer.y + deltay] = 1;
            }
        }
    }

    private long bfs(int[][] grid, Coordinate reindeer, Coordinate exit){
        long answer = Long.MAX_VALUE;
        Queue<String> queue = new PriorityQueue<>();
        queue.add(reindeer.x + " " + reindeer.y + " " + Direction.RIGHT.name() + " " + 0 + " S");
        Map<String, Integer> cache = new HashMap<>();
        while(!queue.isEmpty()){
            String element = queue.poll();
            String[] parts = element.split(" ");
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            Direction direction = Direction.valueOf(parts[2]);
            int current = Integer.parseInt(parts[3]);
            String currentPath = parts[4];
            if(x == exit.x && y == exit.y){
                answer = Math.min(answer, current);
            }
            else{
                int newx = x + direction.dx;
                int newy = y + direction.dy;
                if(safe(newx, newy, grid) && grid[newx][newy] != 2) {
                    Coordinate newReindeer = new Coordinate(newx, newy);
                    String newHash = newReindeer.x + " " + newReindeer.y + " " + direction.name() + " " + (current + 1) + " " + currentPath + "F";
                    if((cache.getOrDefault(newReindeer.x + " " + newReindeer.y + " " + direction.name(), Integer.MAX_VALUE) > current + 1)){
                        queue.add(newHash);
                        cache.put(newReindeer.x + " " + newReindeer.y + " " + direction.name(), current + 1);
                    }
                }
                String right = x + " " + y + " " + direction.turnRight().name() + " " + (current + 1000) + " " + currentPath + "R";
                if((cache.getOrDefault(x + " " + y + " " + direction.turnRight().name(), Integer.MAX_VALUE) > current + 1000) && !currentPath.endsWith("RRR")){
                    queue.add(right);
                    cache.put(x + " " + y + " " + direction.turnRight().name(), current + 1000);
                }
                String right3 = x + " " + y + " " + direction.turnLeft().name() + " " + (current + 1000) + " "+ currentPath + "RRR";
                if((cache.getOrDefault(x + " " + y + " " + direction.turnLeft().name(), Integer.MAX_VALUE) > current + 1000) && !currentPath.endsWith("R")){
                    queue.add(right3);
                    cache.put(x + " " + y + " " + direction.turnLeft().name(), current + 1000);
                }
            }
        }
        return answer;
    }

    private List<String> findOthers(int[][] grid, Coordinate reindeer, Coordinate exit, String best){
        long answer = Long.MAX_VALUE;
        List<String> result = new ArrayList<>();
        Queue<String> queue = new PriorityQueue<>();
        queue.add(reindeer.x + " " + reindeer.y + " " + Direction.RIGHT.name() + " " + 0 + " S");
        Map<String, Integer> cache = new HashMap<>();
        while(!queue.isEmpty()){
            String element = queue.poll();
            String[] parts = element.split(" ");
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            Direction direction = Direction.valueOf(parts[2]);
            int current = Integer.parseInt(parts[3]);
            String currentPath = parts[4];
            if(x == exit.x && y == exit.y){
                if(current < answer){
                    result = new ArrayList<>();
                    answer = current;
                }
                else{
                    if(current == answer){
                        result.add(currentPath);
                    }
                }
            }
            else{
                int newx = x + direction.dx;
                int newy = y + direction.dy;
                if(safe(newx, newy, grid) && grid[newx][newy] != 2) {
                    Coordinate newReindeer = new Coordinate(newx, newy);
                    String newHash = newReindeer.x + " " + newReindeer.y + " " + direction.name() + " " + (current + 1) + " " + currentPath + "F";
                    if((cache.getOrDefault(newReindeer.x + " " + newReindeer.y + " " + direction.name(), Integer.MAX_VALUE) >= current + 1)){
                        queue.add(newHash);
                        cache.put(newReindeer.x + " " + newReindeer.y + " " + direction.name(), current + 1);

                    }
                }
                String right = x + " " + y + " " + direction.turnRight().name() + " " + (current + 1000) + " " + currentPath + "R";
                if((cache.getOrDefault(x + " " + y + " " + direction.turnRight().name(), Integer.MAX_VALUE) >= current + 1000) && !currentPath.endsWith("RRR")){
                    queue.add(right);
                    cache.put(x + " " + y + " " + direction.turnRight().name(), current + 1000);
                }
                String right3 = x + " " + y + " " + direction.turnLeft().name() + " " + (current + 1000) + " "+ currentPath + "RRR";
                if((cache.getOrDefault(x + " " + y + " " + direction.turnLeft().name(), Integer.MAX_VALUE) >= current + 1000) && !currentPath.endsWith("R")){
                    queue.add(right3);
                    cache.put(x + " " + y + " " + direction.turnLeft().name(), current + 1000);
                }
            }
        }
        return result;
    }

    private String bestPath(int[][] grid, Coordinate reindeer, Coordinate exit){
        long answer = Long.MAX_VALUE;
        String result = "";
        Queue<String> queue = new PriorityQueue<>();
        queue.add(reindeer.x + " " + reindeer.y + " " + Direction.RIGHT.name() + " " + 0 + " S");
        Map<String, Integer> cache = new HashMap<>();
        while(!queue.isEmpty()){
            String element = queue.poll();
            String[] parts = element.split(" ");
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            Direction direction = Direction.valueOf(parts[2]);
            int current = Integer.parseInt(parts[3]);
            String currentPath = parts[4];
            if(x == exit.x && y == exit.y){
                if(answer > current){
                    result = currentPath;
                    answer = current;
                }
            }
            else{
                int newx = x + direction.dx;
                int newy = y + direction.dy;
                if(safe(newx, newy, grid) && grid[newx][newy] != 2) {
                    Coordinate newReindeer = new Coordinate(newx, newy);
                    String newHash = newReindeer.x + " " + newReindeer.y + " " + direction.name() + " " + (current + 1) + " " + currentPath + "F";
                    if((cache.getOrDefault(newReindeer.x + " " + newReindeer.y + " " + direction.name(), Integer.MAX_VALUE) > current + 1)){
                        queue.add(newHash);
                        cache.put(newReindeer.x + " " + newReindeer.y + " " + direction.name(), current + 1);
                    }
                }
                String right = x + " " + y + " " + direction.turnRight().name() + " " + (current + 1000) + " " + currentPath + "R";
                if((cache.getOrDefault(x + " " + y + " " + direction.turnRight().name(), Integer.MAX_VALUE) > current + 1000) && !currentPath.endsWith("RRR")){
                    queue.add(right);
                    cache.put(x + " " + y + " " + direction.turnRight().name(), current + 1000);
                }
                String right3 = x + " " + y + " " + direction.turnLeft().name() + " " + (current + 1000) + " "+ currentPath + "RRR";
                if((cache.getOrDefault(x + " " + y + " " + direction.turnLeft().name(), Integer.MAX_VALUE) > current + 1000) && !currentPath.endsWith("R")){
                    queue.add(right3);
                    cache.put(x + " " + y + " " + direction.turnLeft().name(), current + 1000);
                }
            }
        }
        return result;
    }

}