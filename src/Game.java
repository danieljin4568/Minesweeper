import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Game {
    private int[][] playerCells;
    private int[][] solvedCells;
    private int mines;
    private boolean running;

    public record Point(int x, int y) {}

    public Game(int rows, int cols, int mines) {
        this.mines = mines;
        playerCells = new int[rows][cols]; // 0-8, -2 (flag), -1 (unknown)

        for (int[] row : playerCells) {
            Arrays.fill(row, -1);
        }

        solvedCells = generateSolvedCells(rows, cols, mines);

        running = true;
    }

    public int[][] getPlayerCells() {
        return playerCells;
    }

    public boolean isRunning() {
        return running;
    }

    public void dig(int x, int y) {
        if (playerCells[y][x] != -1) return;

        if (solvedCells[y][x] == 9) {
            running = false;

            // reveal all mines
            for (int y1 = 0; y1 < solvedCells.length; y1++) {
                for (int x1 = 0; x1 < solvedCells[0].length; x1++) {
                    if (solvedCells[y1][x1] == 9) {
                        playerCells[y1][x1] = solvedCells[y1][x1];
                    }
                }
            }
        } else {
            List<Point> pointsToDig = findCluster(solvedCells, new Point(x, y), new Point(x, y), new ArrayList<>());
//            pointsToDig.forEach(p -> System.out.printf("[%d, %d], ", p.x(), p.y()));
//            System.out.println();
            pointsToDig.forEach(p -> playerCells[p.y()][p.x()] = solvedCells[p.y()][p.x()]);

            // check if player wins
            int numUnknownCells = Arrays.stream(playerCells)
                    .map(row -> Arrays.stream(row).filter(cell -> cell == -1 || cell == -2).count())
                    .reduce(0L, Long::sum)
                    .intValue();

            if (numUnknownCells == mines) {
                running = false;
                for (int y1 = 0; y1 < playerCells.length; y1++) {
                    for (int x1 = 0; x1 < playerCells[0].length; x1++) {
                        if (playerCells[y1][x1] == -1) {
                            playerCells[y1][x1] = -2;
                        }
                    }
                }
            }
        }
    }

    public void flag(int x, int y) {
        if (playerCells[y][x] == -1) {
            playerCells[y][x] = -2;
        } else if (playerCells[y][x] == -2) {
            playerCells[y][x] = -1;
        }
    }

    private int[][] generateSolvedCells(int rows, int cols, int mines) {
        int[][] cells = new int[rows][cols];
        Random random = new Random();
        int max = rows * cols;
        List<Integer> minePositions = new ArrayList<>();

        for (int i = 0; i< mines; i++) {
            int position;
            do {
                position = random.nextInt(max);
            } while (minePositions.contains(position));

            minePositions.add(position);
            int x = position % cols;
            int y = position / cols;
            cells[y][x] = 9;
        }
//        List<Integer> minePositions = List.of(10, 11, 15, 38, 23, 68, 56, 16, 20, 75);
//        for (int position : minePositions) {
//            int x = position % cols;
//            int y = position / cols;
//            cells[y][x] = 9;
//        }
//        System.out.println(minePositions);

        for (int y=0; y<cells.length; y++) {
            for (int x = 0; x < cells[0].length; x++) {
                if (cells[y][x] == 9) continue;
                Point[] nearbyPoints = {
                        new Point(x-1,y-1),
                        new Point(x,y-1),
                        new Point(x+1,y-1),
                        new Point(x+1,y),
                        new Point(x+1,y+1),
                        new Point(x,y+1),
                        new Point(x-1,y+1),
                        new Point(x-1,y)
                };

                cells[y][x] = (int) Arrays.stream(nearbyPoints)
                        .filter(p -> p.x() >= 0 && p.x() < cells[0].length)
                        .filter(p -> p.y() >= 0 && p.y() < cells.length)
                        .map(p -> cells[p.y()][p.x()])
                        .filter(cell -> cell == 9)
                        .count();
            }
        }

//        Arrays.stream(cells).forEach(row -> System.out.println(Arrays.toString(row)));
        return cells;
    }

    private List<Point> findCluster(int[][] cells, Point initPoint, Point pointOfInterest, List<Point> traveledPoints) {
        int x = pointOfInterest.x();
        int y = pointOfInterest.y();

        traveledPoints.add(pointOfInterest);
        if (traveledPoints.size() > 1 && cells[y][x] != 0) return traveledPoints;

        Point[] nearbyPoints = {
                new Point(x-1,y-1),
                new Point(x,y-1),
                new Point(x+1,y-1),
                new Point(x+1,y),
                new Point(x+1,y+1),
                new Point(x,y+1),
                new Point(x-1,y+1),
                new Point(x-1,y)
        };

        for (Point p : nearbyPoints) {
            if (p.x() < 0 || p.x() > cells[0].length - 1) continue;
            if (p.y() < 0 || p.y() > cells.length - 1) continue;
            if (traveledPoints.stream().anyMatch(tp -> tp.x() == p.x() && tp.y() == p.y())) continue;
            if (cells[p.y()][p.x()] == 9) continue;
            if (x == initPoint.x() && y == initPoint.y() && cells[initPoint.y()][initPoint.x()] != 0 && cells[p.y()][p.x()] != 0) continue;
            traveledPoints = findCluster(cells, initPoint, p, traveledPoints);
        }

        return traveledPoints;
    }
}
