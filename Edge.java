package maze;

import javafx.util.Pair;

import java.util.List;

public class Edge {
    int id1;
    int id2;
    int weight;

    public Edge(int id1, int id2, int weight) {
        this.id1 = id1;
        this.id2 = id2;
        this.weight = weight;
    }

    public Pair<Integer, Integer> getCoordinates(Graph graph) {
        Pair<Integer, Integer> id1Coordinates = graph.getCoordinatesForId(id1);
        Pair<Integer, Integer> id2Coordinates = graph.getCoordinatesForId(id2);
        Pair<Integer, Integer> coordinates;

        if (id1Coordinates.getKey().equals(id2Coordinates.getKey())) {
            int x = (id1Coordinates.getValue() + id2Coordinates.getValue()) / 2;
            coordinates = new Pair<>(id1Coordinates.getKey(), x);
        } else {
            int y = (id1Coordinates.getKey() + id2Coordinates.getKey()) / 2;
            coordinates = new Pair<>(y, id1Coordinates.getValue());
        }

        return coordinates;
    }

    public boolean containsBoth(List<Integer> tree) {
        return tree.contains(id1) && tree.contains(id2);
    }

    public int getId1() {
        return id1;
    }

    public int getId2() {
        return id2;
    }

    public int getWeight() {
        return weight;
    }
}
