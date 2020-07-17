package maze;

import javafx.util.Pair;

import java.util.*;

public class MazeGenerator {
    Graph graph;
    int[][] maze;
    int mazeHeight;
    int mazeLength;


    /**
     * Создаётся лабиринт с заданными размерами и заполняется матрица
     * @param y высота
     * @param x длина
     */
    public MazeGenerator(int y, int x) {
        this.maze = new int[y][x];
        mazeHeight = y % 2 == 0 ? y - 1 : y;
        mazeLength = x % 2 == 0 ? x - 1 : x;
        graph = Graph.getGraphForUnreadyMaze(mazeHeight, mazeLength);
    }

    /**
     * Заполняет лабиринт стенами, генерирует его и возвращает результат
     * @return
     */
    public int[][] getMaze() {
        fillMaze();
        createMaze();
        return maze;
    }

    private void addEntries() {
        Random random = new Random();
        Pair<Integer, Integer> firstEntryCoordinates = new Pair<>(0, 0);
        while (maze[firstEntryCoordinates.getKey()][1] != Maze.PASS) {
            firstEntryCoordinates = new Pair<>(random.nextInt(maze.length - 1) + 1, 0);
        }

        Pair<Integer, Integer> secondEntryCoordinates = new Pair<>(0, maze[0].length - 1);
        if (maze[0].length % 2 == 0) {
            while (maze[secondEntryCoordinates.getKey()][maze[0].length - 3] != Maze.PASS) {
                secondEntryCoordinates = new Pair<>(random.nextInt(maze.length - 1) + 1, maze[0].length - 1);
                setPass(new Pair<>(secondEntryCoordinates.getKey(), secondEntryCoordinates.getValue() - 1));
            }
        } else {
            while (maze[secondEntryCoordinates.getKey()][maze[0].length - 2] != Maze.PASS) {
                secondEntryCoordinates = new Pair<>(random.nextInt(maze.length - 1) + 1, maze[0].length - 1);
            }
        }


        setPass(firstEntryCoordinates);
        setPass(secondEntryCoordinates);
    }


    /**
     * Создаёт лабиринт:
     * - Заполняет его полностью стенами
     * - Добавляет в дерево индекс случайного узла, а в очередь самое легкое его ребро
     * - Пока очередь не опустошится:
     *   ^Достаёт самое легкое ребро и, если дерево содержит только один из связаных элементов,
     *    добавляет пробел на месте этого ребра в лабиринте
     *   ^Получает новые легчайшие ребра узлов, которые связывало это ребро и, если ребра у
     *    этих узлов остались (объект не равен null), добавляет их в очередь
     * - "Прогрызает" в лабиринте места, где находятся узлы
     * - Добавляет нижнюю и правую стену, так как, если кол-во ячеек у лабиринта в длину или
     *   высоту чётное, их может не быть
     * - Добавляет входы в лабиринт
     */
    private void createMaze() {
        fillMaze();

        //Представляет остовное дерево, содержит индексы узлов
        ArrayList<Integer> tree = new ArrayList<>();

        //Очередь, которая хранит рёбра узлов из дерева с наименьшим весом
        PriorityQueue<Edge> edges = new PriorityQueue<>(new Comparator<Edge>() {
            @Override
            public int compare(Edge edge, Edge t1) {
                return edge.getWeight() - t1.getWeight();
            }
        });

        tree.add(graph.randomId());                                 //Индекс случайного узла
        edges.add(graph.getLightestEdge(tree.get(0), tree));        //Самое легкое ребро случайного узла

        while (!edges.isEmpty()) {
            Edge edge = edges.poll();
            if (!edge.containsBoth(tree)) {
                setPass(edge.getCoordinates(graph));
                tree.add(edge.getId2());
            }

            /**
             * Получает новые легчайшие ребра узлов, которые связывало это ребро и, если ребра у
             * этих узлов остались (объект не равен null), добавляет их в очередь
             */
            Edge first = graph.getLightestEdge(edge.getId1(), tree);
            Edge second = graph.getLightestEdge(edge.getId2(), tree);
            if (first != null) {
                edges.add(first);
            }
            if (second != null) {
                edges.add(second);
            }
        }

        for (int vertexId : tree) {
            int x = vertexId % (mazeLength / 2) * 2 + 1;
            int y = vertexId / (mazeLength / 2) * 2 + 1;
            setPass(new Pair<>(y, x));
        }

        addEntries();
    }

    private void fillMaze() {
        for (int i = 0; i < maze.length; i++) {
            Arrays.fill(maze[i], Maze.WALL);
        }
    }

    private void setPass(Pair<Integer, Integer> coordinates) {
        maze[coordinates.getKey()][coordinates.getValue()] = Maze.PASS;
    }

    /**
     * Находит самое лёгкое ребро, которое ещё не добавлено в дерево, для переданого индекса узла
     * @param index индекс узла
     * @param tree список добавленых в дерево индексов
     * @return самое лёгкое ребро
     */

    /**
     * Заполняет матрицу
     * Рандомными значениями в местах, где рёбра есть
     * Нолями в пересечениях с самим собой
     * -1 в местах где рёбра отсутствуют
     *
     * Для каждого узла проверяет только соседей снизу и справа
     */
}
