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
            List<String> otherBests = findOthers(grid, reindeer, exit);
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
        for(Arrival arrival: search(grid, reindeer, exit, false)){
            answer = Math.min(answer, arrival.cost());
        }
        return answer;
    }

    private List<String> findOthers(int[][] grid, Coordinate reindeer, Coordinate exit){
        long answer = Long.MAX_VALUE;
        List<String> result = new ArrayList<>();
        for(Arrival arrival: search(grid, reindeer, exit, true)){
            if(arrival.cost() < answer){
                result = new ArrayList<>();
                answer = arrival.cost();
            }
            else if(arrival.cost() == answer){
                result.add(arrival.path());
            }
        }
        return result;
    }

    private String bestPath(int[][] grid, Coordinate reindeer, Coordinate exit){
        long answer = Long.MAX_VALUE;
        String result = "";
        for(Arrival arrival: search(grid, reindeer, exit, false)){
            if(answer > arrival.cost()){
                result = arrival.path();
                answer = arrival.cost();
            }
        }
        return result;
    }

    /**
     * Every time the search reaches the exit, in order, with the cost and the moves taken (F forward,
     * R turn right, RRR turn left). A state is searched again only at a lower cost than before, or with
     * keepTies at an equal cost too, which lets every best path reach the exit.
     */
    private List<Arrival> search(int[][] grid, Coordinate reindeer, Coordinate exit, boolean keepTies){
        List<Arrival> arrivals = new ArrayList<>();
        Queue<String> queue = new PriorityQueue<>();
        queue.add(reindeer.x + " " + reindeer.y + " " + Direction.RIGHT.name() + " " + 0 + " S");
        Map<String, Integer> cache = new HashMap<>();
        while(!queue.isEmpty()){
            String[] parts = queue.poll().split(" ");
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            Direction direction = Direction.valueOf(parts[2]);
            int current = Integer.parseInt(parts[3]);
            String currentPath = parts[4];
            if(x == exit.x && y == exit.y){
                arrivals.add(new Arrival(current, currentPath));
            }
            else{
                int newx = x + direction.dx;
                int newy = y + direction.dy;
                if(safe(newx, newy, grid) && grid[newx][newy] != 2) {
                    offer(queue, cache, keepTies, newx + " " + newy + " " + direction.name(), current + 1, currentPath + "F");
                }
                if(!currentPath.endsWith("RRR")){
                    offer(queue, cache, keepTies, x + " " + y + " " + direction.turnRight().name(), current + 1000, currentPath + "R");
                }
                if(!currentPath.endsWith("R")){
                    offer(queue, cache, keepTies, x + " " + y + " " + direction.turnLeft().name(), current + 1000, currentPath + "RRR");
                }
            }
        }
        return arrivals;
    }

    /** Queues a state if its cost beats (or, with keepTies, matches) the best cost seen there so far. */
    private void offer(Queue<String> queue, Map<String, Integer> cache, boolean keepTies, String state, int cost, String path){
        int seen = cache.getOrDefault(state, Integer.MAX_VALUE);
        if(seen > cost || (keepTies && seen == cost)){
            queue.add(state + " " + cost + " " + path);
            cache.put(state, cost);
        }
    }

    private record Arrival(int cost, String path){}

}