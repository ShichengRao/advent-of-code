package src.solutions;

import src.meta.DayTemplate;
import src.objects.Coordinate;
import src.objects.Direction;

import java.util.*;

import static src.meta.Utils.*;

public class Day15 extends DayTemplate {

    public String solve(boolean part1, Scanner in) {
        long answer = 0;
        List<List<String>> sections = readSections(in);
        List<String> lines = sections.get(0);
        List<String> movements = sections.get(1);
        if(!part1){
            lines = convert(lines);
        }

        Map<Character, Integer> mapping = Map.of('#',1000,'.',1,'O',2,'[',3,']',4,'@',1);
        int[][] grid = buildGrid(lines, mapping::get);
        Coordinate robot = find(lines, '@');

        StringBuilder allMovements = new StringBuilder();
        for(String move: movements){
            allMovements.append(move);
        }
        for(char c: allMovements.toString().toCharArray()){
            oneCycle(c, robot, grid, part1);
        }
        for(int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == 2 || grid[i][j] == 3) {
                    answer += 100L * j + i;
                }
            }
        }
        return answer + "";
    }

    private static List<String> convert(List<String> lines) {
        List<String> newLines = new ArrayList<>();
        Map<Character, String> mapping = Map.of('#',"##", '.',"..",'O',"[]",'@',"@.");
        for (String line : lines) {
            StringBuilder newLine = new StringBuilder();
            for (char c : line.toCharArray()) {
                newLine.append(mapping.get(c));
            }
            newLines.add(newLine.toString());
        }
        return newLines;
    }

    private void oneCycle(char c, Coordinate robot, int[][] grid, boolean part1){
        Direction direction = Direction.of(c);
        Coordinate goal = robot.move(direction);
        if(grid[goal.x][goal.y] == 1){
            robot.x = goal.x;
            robot.y = goal.y;
            return;
        }
        if(grid[goal.x][goal.y] == 1000){
            return;
        }
        if(direction == Direction.RIGHT || direction == Direction.LEFT || part1){
            boolean someEmpty = false;
            Coordinate firstEmpty = new Coordinate(1,-1);
            int counter = 0;
            while(true){
                counter++;
                goal = goal.move(direction);
                if(grid[goal.x][goal.y] == 1000){
                    break;
                }
                if(grid[goal.x][goal.y] == 1){
                    someEmpty = true;
                    firstEmpty = new Coordinate(goal.x, goal.y);
                    break;
                }
            }
            if(someEmpty){
                while(counter-- > 0){
                    grid[firstEmpty.x][firstEmpty.y] = grid[firstEmpty.x - direction.dx][firstEmpty.y - direction.dy];
                    firstEmpty.x -= direction.dx;
                    firstEmpty.y -= direction.dy;
                }
                robot.x += direction.dx;
                robot.y += direction.dy;
                grid[robot.x][robot.y] = 1;
            }
        }
        else{
            List<Set<Integer>> indices = new ArrayList<>();
            indices.add(new HashSet<>());
            indices.get(0).add(robot.x);
            boolean allEmpty = false;
            int level = robot.y;
            while(!allEmpty) {
                Set<Integer> nextIndices = new HashSet<>();
                level += direction.dy;
                allEmpty = true;
                for (int index : indices.get(indices.size() - 1)) {
                    if (grid[index][level] == 1000) {
                        return;
                    }
                    if (grid[index][level] != 1) {
                        allEmpty = false;
                    }
                    if (grid[index][level] == 3) {
                        nextIndices.add(index + 1);
                        nextIndices.add(index);
                    }
                    if (grid[index][level] == 4) {
                        nextIndices.add(index - 1);
                        nextIndices.add(index);
                    }
                }
                if(!allEmpty){
                    indices.add(nextIndices);
                }
            }
            for(int i = indices.size() - 1; i > 0; i--) {
                Set<Integer> indicesLayer = indices.get(i);
                for(int index: indicesLayer){
                    grid[index][robot.y + ((i + 1) * direction.dy)] = grid[index][robot.y + (i * direction.dy)];
                    grid[index][robot.y + (i * direction.dy)] = 1;
                }
            }
            robot.x += direction.dx;
            robot.y += direction.dy;
        }
    }
}