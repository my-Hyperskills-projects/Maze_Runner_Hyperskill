package maze;

import java.io.*;
import java.util.Scanner;

public class Main {
    static Scanner scanner = new Scanner(System.in);
    static boolean isMazeExist = false;
    static boolean isExit = false;
    static Maze currentMaze;

    public static void main(String[] args) {

        while (!isExit) {
            printMenu();
            int commandId = scanner.nextInt();

            switch (commandId) {
                case 1:
                    generateMaze();
                    currentMaze.printMaze();
                    break;
                case 2:
                    loadMaze();
                    break;
                case 3:
                    saveMaze();
                    break;
                case 4:
                    if (isMazeExist) currentMaze.printMaze();
                    break;
                case 5:
                    currentMaze.findTheEscape();
                    break;
                case 0:
                    isExit = true;
                    System.out.println("Bye!");
                    break;
                default:
                    System.out.println("Incorrect option. Please try again");
            }
        }
    }

    public static void printMenu() {
        System.out.println("\n=== Menu ===\n" +
                "1. Generate a new maze\n" +
                "2. Load a maze");

        if (isMazeExist) {
            System.out.println("3. Save the maze\n" +
                    "4. Display the maze\n" +
                    "5. Find the escape");
        }

        System.out.println("0. Exit");
    }

    public static void generateMaze() {
        System.out.println("Enter the size of a new maze");
        int size = scanner.nextInt();
        currentMaze = new Maze(size, size);
        isMazeExist = true;
    }

    public static void loadMaze() {
        String path = scanner.next();

        try {
            currentMaze = new Maze(new File(path));
            isMazeExist = true;
        } catch (FileNotFoundException e) {
            System.out.printf("The file %s does not exist\n", path);
        } catch (IOException e) {
            System.out.println("Cannot load the maze. It has an invalid format");
        }
    }

    public static void saveMaze() {
        String path = scanner.next();

        if (!isMazeExist) return;

        try (PrintWriter writer = new PrintWriter(new FileWriter(path))) {
            currentMaze.saveMaze(writer);
        } catch (IOException e) {
            System.out.println("Something goes wrong! CODE - RED!!!");
        }
    }
}
