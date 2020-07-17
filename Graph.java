package maze;

import javafx.util.Pair;

import java.util.*;

/**
 * Класс представляет граф для лабиринтов, где узлы условно расставлены в виде сетки
 * и имеют общие рёбра толко со своими непосредственными соседями
 * Объект класса можно получить используя 2 метода:
 * getGraphForUnreadyMaze(int y, int x) - возвращает граф для лабиринта заданных размеров,
 * где каждый узел имеет ребра случайного веса со всеми своими соседями
 * getGraphForReadyMaze(int[][] maze) - возвращает граф на основе переданого лабиринта,
 * где ребра есть только у узлов между которыми нет стенок, а также отсутствуют проходные
 * узлу (имеют свободное место с двух сторон и находятся на прямой дорожке)
 *
 * Граф представляет марица смежности
 * Узлы рассположены в каждой второй ячейке и по умолчанию пустые
 */

public class Graph {
    ArrayList<Pair<Integer, Integer>> vertexes;
    ArrayList<Integer> vertexesWithOneEdge;
    int[][] matrix;
    int[] distancesToStart;
    Pair<Integer, Integer> firstEntry;
    Pair<Integer, Integer> secondEntry;
    int firstEntryId = -1;
    int secondEntryId = -1;

    private Graph() {
        this.vertexes = new ArrayList<>();
        vertexesWithOneEdge = new ArrayList<>();
    }

    public static Graph getGraphForUnreadyMaze(int y, int x) {
        Graph graph = new Graph();
        graph.fillMatrixForUnreadyMaze(y, x);
        graph.fillMapForUnreadyMaze(x);
        return graph;
    }

    public static Graph getGraphForReadyMaze(int[][] maze) {
        Graph graph = new Graph();
        graph.fillMatrixForReadyMaze(maze);
        return graph;
    }

    /**
     * -Находит входы в лабиринт
     * -Добавляет все подходящие узлы в список vertexes, где индекс соответсвует id узла
     * -Создает матрицу на основе размера списка с узлами и заполняет её значениями -1 (вместо бесконечности)
     * -Проверяет ближайщих соседей справа и снизу, если соседние клетки "пустые", то ищет в списке
     *  потенциальные соседние узлы и проверяет на наличие пути к ним в линии и в колонке соответственно и,
     *  если такой есть, заполняет соответствующие ячейки в матрице
     *
     * @param maze переданный лабиринт
     */
    private void fillMatrixForReadyMaze(int[][] maze) {
        findEntries(maze);
        countVertex(maze);

        matrix = new int[vertexes.size()][vertexes.size()];
        for (int i = 0; i < vertexes.size(); i++) {
            Arrays.fill(matrix[i], -1);
        }

        for (int i = 0; i < vertexes.size(); i++) {
            int y = vertexes.get(i).getKey();
            int x = vertexes.get(i).getValue();

            if (maze[y + 1][x] == 0) {
                int fromBottom = 0; //x - fix
                int j = i;

                for (; j < vertexes.size(); j++) {
                    if (vertexes.get(j).getValue() == x && vertexes.get(j).getKey() > y) {
                        fromBottom = vertexes.get(j).getKey();
                        break;
                    }
                }

                if (isHavePathInColumn(maze, x, y, fromBottom)) {
                    int weight = Math.abs(fromBottom - vertexes.get(i).getKey());
                    matrix[i][j] = weight;
                    matrix[j][i] = weight;
                }

            }

            if (maze[y][x + 1] == 0) {
                if (i != matrix.length - 1) {
                    int fromRight = vertexes.get(i + 1).getValue();
                    if (isHavePathInLine(maze, y, x, fromRight)) {
                        int weight = Math.abs(vertexes.get(i + 1).getValue() - vertexes.get(i).getValue());
                        matrix[i][i + 1] = weight;
                        matrix[i + 1][i] = weight;
                    }
                }
            }

            matrix[i][i] = 0;
        }
    }

    /**
     * Проверяет на наличие пути в колонке
     * @param maze - лабиринт
     * @param x - номер колонки в лабиринте
     * @param y1 - номер линии 1-го узла
     * @param y2 - номер линии 2-го узла
     * @return имеется путь или нет
     */
    private boolean isHavePathInColumn(int[][] maze, int x, int y1, int y2) {
        int from = Math.min(y1, y2) + 1;
        int to = Math.max(y1, y2);

        while (from < to) {
            if (maze[from][x] == 1) return false;
            from++;
        }

        return true;
    }

    /**
     * Проверяет на наличие пути в линии
     * @param maze - лабиринт
     * @param y - номер линии узлов
     * @param x1 - номер конки 1-го узла
     * @param x2 - номер конки 2-го узла
     * @return имеется путь или нет
     */
    private boolean isHavePathInLine(int[][] maze, int y, int x1, int x2) {
        int from = Math.min(x1, x2) + 1;
        int to = Math.max(x1, x2);

        while (from < to) {
            if (maze[y][from] == 1) return false;
            from++;
        }

        return true;
    }

    /**
     * Добавляет подходящие узлы в список, а также назначает узлы ответственные за входы
     *
     * Во внешнем цикле перебираются линии, во внутреннем ячейки этих линий с шагом через одну
     * В начале каждого цикла производится проверка на соответствие координатам входов,
     * если совпадают, то узел добавляется в список, а присваивается соответствующей переменной и
     * цикл дальше не идёт.
     *
     * После с помощью нескольких проверок исключаются лишнии узлы
     *
     * @param maze - лабиринт
     */
    private void countVertex(int[][] maze) {
        for (int i = 1; i < maze.length; i += 2) {

            if (maze.length % 2 == 0 && i == maze.length - 1) {
                break;
            }

            for (int j = 1; j < maze[i].length; j += 2) {

                if (maze[0].length % 2 == 0 && j == maze[0].length - 1) {
                    break;
                }

                if (i == firstEntry.getKey()) {
                    if (j == 1) {
                        firstEntryId = vertexes.size();
                        vertexes.add(new Pair<>(i, j));
                        if (i != secondEntry.getKey()) continue;
                    }
                }

                if (i + 1 == firstEntry.getKey()) {
                    if (j == 1) {
                        firstEntryId = vertexes.size();
                        vertexes.add(new Pair<>(i + 1, j));
                        if (i + 1 != secondEntry.getKey()) continue;
                    }
                }

                if (i == secondEntry.getKey()) {
                    if (j == secondEntry.getValue()) {
                        secondEntryId = vertexes.size();
                        vertexes.add(new Pair<>(i, j));
                        continue;
                    }
                }

                if (i + 1 == secondEntry.getKey()) {
                    if (j == secondEntry.getValue()) {
                        secondEntryId = vertexes.size();
                        vertexes.add(new Pair<>(i + 1, j));
                        continue;
                    }
                }

                boolean left = false;
                boolean right = false;
                boolean top = false;
                boolean bottom = false;
                int score = 0;

                if (maze[i - 1][j] == 0) {
                    top = true;
                    score++;
                }
                if (maze[i + 1][j] == 0) {
                    bottom = true;
                    score++;
                }
                if (maze[i][j - 1] == 0) {
                    left = true;
                    score++;
                }
                if (maze[i][j + 1] == 0) {
                    right = true;
                    score++;
                }

                if (score == 2 && ((right && left) || (top && bottom))) continue;
                vertexes.add(new Pair<>(i, j));

            }
        }
    }

    public void print() {
        for (int i = 0; i < matrix.length; i++) {
            System.out.println(Arrays.toString(matrix[i]));
        }
    }

    /**
     * Проходится по всем узлам и считает расстояние до них от первого входа (каждая ячейка = 1),
     * расстояние помещается в соответсвующую их id ячейку в массиве distancesToStart
     * @return массив с расстояниями до первого входа
     */
    public int[] getDistancesToStart() {
        // TODO
        ArrayList<Integer> visitedVertexes = new ArrayList<>();
        Stack<Pair<Integer, Integer>> stack = new Stack<>();         //id и расстояние
        distancesToStart = new int[matrix.length];

        stack.push(new Pair<>(firstEntryId, 0));
        visitedVertexes.add(firstEntryId);

        while (!stack.empty()) {
            System.out.println();
            Pair<Integer, Integer> vertex = stack.peek();
            int distance = vertex.getValue();
            int id = vertex.getKey();

            int minDistance = Integer.MAX_VALUE;
            int nextVertexId = -1;
            for (int i = 0; i < matrix.length; i++) {
                if (!visitedVertexes.contains(i) && matrix[i][id] > 0 && matrix[i][id] < minDistance) {
                    minDistance = matrix[i][id];
                    nextVertexId = i;
                }
            }

            if (nextVertexId == -1) {
                stack.pop();
                continue;
            }

            if (!visitedVertexes.contains(nextVertexId)) {
                visitedVertexes.add(nextVertexId);
            }
            distancesToStart[nextVertexId] = distance + minDistance;
            stack.push(new Pair<>(nextVertexId, distance + minDistance));
        }


        return distancesToStart;
    }

    /**
     * Ищет путь между входами
     * Начинает со второго входа
     * В цикле выбирается сосед с наименьшим расстоянием до 1-го входа и так пока
     * этим соседом не окажется сам 1-ый вход
     * @return список с координатами узлов через которые идет путь
     */
    public ArrayList<Pair<Integer, Integer>> getPath() {
        distancesToStart = getDistancesToStart();

        ArrayList<Pair<Integer, Integer>> path = new ArrayList<>();
        int currentVertexId = secondEntryId;

        while (currentVertexId != firstEntryId) {
            int nextVertexId = -1;
            int distance = Integer.MAX_VALUE;

            for (int i = 0; i < matrix.length; i++) {
                if (matrix[currentVertexId][i] > 0) {
                    if (distancesToStart[i] < distance) {
                        distance = distancesToStart[i];
                        nextVertexId = i;
                    }
                }
            }


            path.add(vertexes.get(currentVertexId));
            currentVertexId = nextVertexId;
        }
        path.add(vertexes.get(currentVertexId));
        return path;
    }

    /**
     * Ищет и назначает координаты входов в лабиринт
     * @param maze - лабиринт
     */
    private void findEntries(int[][] maze) {
        for (int i = 1; i < maze.length; i++) {
            if (maze[i][0] == 0) {
                firstEntry = new Pair<>(i, 1);
            }

            if (maze[i][maze[0].length - 1] == 0) {
                if (maze[0].length % 2 == 0) {
                    secondEntry = new Pair<>(i, maze[0].length - 3);
                } else {
                    secondEntry = new Pair<>(i, maze[0].length - 2);
                }
            }
        }
    }

    /**
     * -Создаёт матрицу смежности для лабиринта заданого размера и заполняет её значениями -1
     * -Если соседи снизу и справа есть, то назначает ребру между тек.узлом и соседом случайный вес
     * -Вес ребра для узла с самим собой равен 0
     *
     * @param y - кол-во линий в лабиринте
     * @param x - кол-во столбцов
     */
    private void fillMatrixForUnreadyMaze(int y, int x) {
        Random random = new Random();
        int vertexHeight = y / 2;
        int vertexLength = x / 2;
        int vertexCount = vertexHeight * vertexLength;
        matrix = new int[vertexCount][vertexCount];

        for (int i = 0; i < vertexCount; i++) {
            Arrays.fill(matrix[i], -1);
        }

        for (int i = 0; i < vertexCount; i++) {

            //Если имеется сосед справа и кол-во узлов больше, чем индекс тек. элем. + 1
            if ((i + 1) % vertexLength != 0) {
                if (vertexCount > i + 1) {
                    int randomInt = random.nextInt(10) + 1;
                    matrix[i][i + 1] = randomInt;
                    matrix[i + 1][i] = randomInt;
                }
            }

            //Если имеется сосед снизу и кол-во узлов больше, чем индекс тек. элем. + кол-во узлов в одной линии
            if (i < vertexCount) {
                if (vertexCount > i + vertexLength) {
                    int randomInt = random.nextInt(10) + 1;
                    matrix[i][i + vertexLength] = randomInt;
                    matrix[i + vertexLength][i] = randomInt;
                }
            }

            matrix[i][i] = 0;
        }
    }

    /**
     * Заполняет список с координатами на основе id узлов в графе для создания лабиринта
     *
     * @param x - id узла
     */
    private void fillMapForUnreadyMaze(int x) {
        for (int i = 0; i < matrix.length; i++) {
            int height = i / (x / 2) * 2 + 1; //y
            int length = i % (x / 2) * 2 + 1;     //x
            vertexes.add(new Pair<>(height, length));
        }
    }

    /**
     * Возвращает самое лёгкое ребро для узла с переданным id
     * @param index - id узла
     * @param tree - список с элементами, которые находятся в дереве
     * @return самое лёгкое ребро
     */
    public Edge getLightestEdge(int index, ArrayList<Integer> tree) {
        int minWeight = Integer.MAX_VALUE;
        int id = -1;

        for (int i = 0; i < matrix.length; i++) {
            if (matrix[index][i] > 0 && matrix[index][i] < minWeight && !tree.contains(i)) {
                minWeight = matrix[index][i];
                id = i;
            }
        }


        return id == -1 ? null : new Edge(index, id, minWeight);
    }

    public int randomId() {
        Random random = new Random();
        return random.nextInt(matrix.length);
    }

    /**
     * Возвращает координаты для переданного id узла
     * @param id узла
     * @return Пара координат <y, x>
     */
    public Pair<Integer, Integer> getCoordinatesForId(int id) {
        return vertexes.get(id);
    }

    public Pair<Integer, Integer> getFirstEntry() {
        return firstEntry;
    }

    public Pair<Integer, Integer> getSecondEntry() {
        return secondEntry;
    }
}
