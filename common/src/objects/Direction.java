package src.objects;

/**
 * The four grid directions in clockwise order, for grids laid out like Utils.buildGrid:
 * x is the column and y is the row, so UP lowers y.
 */
public enum Direction {
    UP(0, -1), RIGHT(1, 0), DOWN(0, 1), LEFT(-1, 0);

    private static final Direction[] CLOCKWISE = values();

    public final int dx;
    public final int dy;

    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public Direction turnRight() {
        return CLOCKWISE[(ordinal() + 1) % 4];
    }

    public Direction turnLeft() {
        return CLOCKWISE[(ordinal() + 3) % 4];
    }

    public Direction opposite() {
        return CLOCKWISE[(ordinal() + 2) % 4];
    }

    /** The direction an arrow (^ > v <) or letter (U R D L, or N E S W) points. */
    public static Direction of(char c) {
        return switch (c) {
            case '^', 'U', 'N' -> UP;
            case '>', 'R', 'E' -> RIGHT;
            case 'v', 'D', 'S' -> DOWN;
            case '<', 'L', 'W' -> LEFT;
            default -> throw new IllegalArgumentException("Not a direction: " + c);
        };
    }
}
