package maze;

import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class Maze {
    int[][] maze;
    boolean isSolved = false;
    final static int WALL = 1;
    final static int PASS = 0;
    final static int PATH = -1;

    public Maze(File file) throws IOException {
        Scanner reader = new Scanner(file);
        ArrayList<String> lines = new ArrayList<>();

        while (reader.hasNextLine()) {
            String line = reader.nextLine();
            lines.add(line);
        }

        maze = new int[lines.size()][lines.get(0).length()];

        for (int i = 0; i < lines.size(); i++) {
            for (int j = 0; j < lines.get(i).length(); j++) {
                maze[i][j] = lines.get(i).charAt(j) == '0' ? 0 : 1;
            }
        }

        reader.close();
    }

    public Maze(int y, int x) {
        MazeGenerator generator = new MazeGenerator(y, x);
        this.maze = generator.getMaze();
    }

    public void printMaze() {
        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[i].length; j++) {
                if (maze[i][j] == PASS || maze[i][j] == PATH) {
                    System.out.print("  ");
                } else if (maze[i][j] == WALL) {
                    System.out.print("\u2588\u2588");
                }
            }
            System.out.println();
        }
    }

    public void printSolvedMaze() {
        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[i].length; j++) {
                if (maze[i][j] == PASS) {
                    System.out.print("  ");
                } else if (maze[i][j] == WALL) {
                    System.out.print("\u2588\u2588");
                } else if (maze[i][j] == PATH) {
                    System.out.print("//");
                }
            }
            System.out.println();
        }
    }

    public void saveMaze(PrintWriter writer) throws IOException {
        for (int[] array : maze) {
            String str = Arrays.toString(array).replaceAll(", |\\[|\\]", "");
            writer.printf("%s\n", str);
        }
    }

    public void findTheEscape() {
        if (!isSolved) {
            Graph graph = Graph.getGraphForReadyMaze(this.maze);
            ArrayList<Pair<Integer, Integer>> path = graph.getPath();

            for (int i = 0; i < path.size() - 1; i++) {
                int min;
                int max;
                if (path.get(i).getKey() == path.get(i + 1).getKey()) {
                    min = Math.min(path.get(i).getValue(), path.get(i + 1).getValue());
                    max = Math.max(path.get(i).getValue(), path.get(i + 1).getValue());
                    for (int j = min; j <= max; j++) {
                        maze[path.get(i).getKey()][j] = PATH;
                    }
                } else {
                    min = Math.min(path.get(i).getKey(), path.get(i + 1).getKey());
                    max = Math.max(path.get(i).getKey(), path.get(i + 1).getKey());
                    for (int j = min; j <= max; j++) {
                        maze[j][path.get(i).getValue()] = PATH;
                    }
                }
            }

            Pair<Integer, Integer> firstEntry = graph.getFirstEntry();
            Pair<Integer, Integer> secondEntry = graph.getSecondEntry();
            maze[firstEntry.getKey()][firstEntry.getValue() - 1] = PATH;
            maze[secondEntry.getKey()][secondEntry.getValue() + 1] = PATH;
        }
        this.printSolvedMaze();
    }
}