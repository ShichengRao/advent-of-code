package src.meta;

import src.objects.Coordinate;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.function.IntPredicate;
import java.util.function.ToIntFunction;
import java.util.regex.Pattern;

public class Utils {
    /** A whole number; a minus sign counts only when it doesn't follow a digit, so "3-5" is 3 and 5. */
    private static final Pattern NUMBER = Pattern.compile("(?<!\\d)-?\\d+");

    private Utils() {
    }

    public static long gcd(long a, long b) {
        if (b == 0) return a;
        return gcd(b, a % b);
    }

    public static long lcm(long a, long b) {
        return a * b / gcd(a, b);
    }

    public static int gcd(int a, int b) {
        if (b == 0) return a;
        return gcd(b, a % b);
    }

    public static int lcm(int a, int b) {
        return a * b / gcd(a, b);
    }

    public static boolean isPrime(int n) {
        if (n <= 1) return false;
        if (n <= 3) return true;
        if (n % 2 == 0 || n % 3 == 0) return false;
        for (int i = 5; i * i <= n; i += 6) {
            if (n % i == 0 || n % (i + 4) == 0) return false;
        }
        return true;
    }

    public static int gcd(int[] nums) {
        int result = nums[0];
        for (int i = 1; i < nums.length; i++) {
            result = gcd(result, nums[i]);
        }
        return result;
    }

    public static int lcm(int[] nums){
        int result = nums[0];
        for (int i = 1; i < nums.length; i++) {
            result = lcm(result, nums[i]);
        }
        return result;
    }

    public static long gcd(long[] nums) {
        long result = nums[0];
        for (int i = 1; i < nums.length; i++) {
            result = gcd(result, nums[i]);
        }
        return result;
    }

    public static long lcm(long[] nums){
        long result = nums[0];
        for (int i = 1; i < nums.length; i++) {
            result = lcm(result, nums[i]);
        }
        return result;
    }

    public static boolean safe(int x, int y, int[][] grid){
        return x >= 0 && x < grid.length && y >= 0 && y < grid[0].length;
    }

    public static boolean safe(int x, int y, char[][] grid){
        return x >= 0 && x < grid.length && y >= 0 && y < grid[0].length;
    }

    public static List<Coordinate> getNeighbors(int x, int y, int[][] grid){
        List<Coordinate> neighbors = new ArrayList<>();
        int[] xs = {1,-1,0,0};
        int[] ys = {0,0,1,-1};
        for (int i = 0; i < 4; i++) {
            int nx = x + xs[i];
            int ny = y + ys[i];
            if (safe(nx, ny, grid)) {
                neighbors.add(new Coordinate(nx, ny));
            }
        }
        return neighbors;
    }

    public static List<Coordinate> getNeighborsFull(int x, int y, int[][] grid){
        List<Coordinate> neighbors = new ArrayList<>();
        int[] xs = {1,1,1,0,0,-1,-1,-1};
        int[] ys = {1,0,-1,1,-1,1,0,-1};
        for (int i = 0; i < xs.length; i++) {
            int nx = x + xs[i];
            int ny = y + ys[i];
            if (safe(nx, ny, grid)) {
                neighbors.add(new Coordinate(nx, ny));
            }
        }
        return neighbors;
    }

    public static int[][] convertCharArrayToIntArray(char[][] charArray, ToIntFunction<Character> converter) {
        int[][] result = new int[charArray.length][charArray[0].length];
        for (int i = 0; i < charArray.length; i++) {
            for (int j = 0; j < charArray[i].length; j++) {
                result[i][j] = converter.applyAsInt(charArray[i][j]);
            }
        }
        return result;
    }



    public static int[][] buildGrid(List<String> lines, ToIntFunction<Character> converter){
        int[][] grid = new int[lines.get(0).length()][lines.size()];
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            for (int j = 0; j < line.length(); j++) {
                grid[j][i] = converter.applyAsInt(line.charAt(j));
            }
        }
        return grid;
    }

    public static char[][] buildGrid(List<String> lines){
        char[][] grid = new char[lines.get(0).length()][lines.size()];
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            for (int j = 0; j < line.length(); j++) {
                grid[j][i] = line.charAt(j);
            }
        }
        return grid;
    }

    public static char[][] getGrid(Scanner in){
        List<String> lines = new ArrayList<>();
        while (in.hasNext()) {
            lines.add(in.nextLine());
        }
        return buildGrid(lines);
    }

    public static long det(long x1, long x2, long y1, long y2){
        return x1 * y2 - x2 * y1;
    }

    /** Every line of the input, blank ones included. */
    public static List<String> readLines(Scanner in) {
        List<String> lines = new ArrayList<>();
        while (in.hasNextLine()) {
            lines.add(in.nextLine());
        }
        return lines;
    }

    /** The input's lines, in groups separated by blank lines. */
    public static List<List<String>> readSections(Scanner in) {
        List<List<String>> sections = new ArrayList<>();
        List<String> section = new ArrayList<>();
        while (in.hasNextLine()) {
            String line = in.nextLine();
            if (!line.isBlank()) {
                section.add(line);
            } else if (!section.isEmpty()) {
                sections.add(section);
                section = new ArrayList<>();
            }
        }
        if (!section.isEmpty()) {
            sections.add(section);
        }
        return sections;
    }

    /**
     * Every whole number in the text, in order. A minus sign counts only when it doesn't
     * follow a digit, so "x=-3" gives -3 but the range "3-5" gives 3 and 5.
     */
    public static long[] longs(String text) {
        return NUMBER.matcher(text).results().mapToLong(m -> Long.parseLong(m.group())).toArray();
    }

    /** Like {@link #longs(String)}, for numbers that fit in an int. */
    public static int[] ints(String text) {
        return NUMBER.matcher(text).results().mapToInt(m -> Integer.parseInt(m.group())).toArray();
    }

    /**
     * Where c first appears, as x = column and y = row (the layout buildGrid uses), or null if
     * it doesn't appear.
     */
    public static Coordinate find(List<String> lines, char c) {
        for (int y = 0; y < lines.size(); y++) {
            int x = lines.get(y).indexOf(c);
            if (x >= 0) {
                return new Coordinate(x, y);
            }
        }
        return null;
    }

    /**
     * Breadth-first search from start, moving up, down, left and right onto cells whose value
     * passes open. Returns the number of steps to each cell, or -1 where it can't be reached.
     */
    public static int[][] distances(int[][] grid, Coordinate start, IntPredicate open) {
        int[][] distance = new int[grid.length][grid[0].length];
        for (int[] column : distance) {
            Arrays.fill(column, -1);
        }
        distance[start.x][start.y] = 0;
        Queue<Coordinate> queue = new ArrayDeque<>();
        queue.add(start);
        while (!queue.isEmpty()) {
            Coordinate current = queue.poll();
            for (Coordinate next : getNeighbors(current.x, current.y, grid)) {
                if (distance[next.x][next.y] == -1 && open.test(grid[next.x][next.y])) {
                    distance[next.x][next.y] = distance[current.x][current.y] + 1;
                    queue.add(next);
                }
            }
        }
        return distance;
    }

    //TODO: kruskal's algorithm, prim's algorithm, dijkstras, dfs

}