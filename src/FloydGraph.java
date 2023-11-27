import java.util.ArrayList;
import java.util.Arrays;

public class FloydGraph {
    int vertices;
    double[][] distance;
    int next[][];
    ArrayList<Integer> path = new ArrayList<>();

    // Vytvoří graf s n vrcholama
    public FloydGraph(int vertices) {
        this.vertices = vertices;
        distance = new double[vertices][vertices];
        next = new int[vertices][vertices];
        for (int i = 0; i < vertices; i++) {
            Arrays.fill(distance[i], Double.MAX_VALUE);
            Arrays.fill(next[i], -1);
            distance[i][i] = 0;
        }
    }

    // Přidání hran do grafu
    public void addEdge(int source, int destination, double weight) {
        distance[source][destination] = weight;
        distance[destination][source] = weight;
        next[source][destination] = destination;
    }


    public void floydWarshall() {
        for (int k = 0; k < vertices; k++) {
            for (int i = 0; i < vertices; i++) {
                for (int j = 0; j < vertices; j++) {
                    if (distance[i][k] != Double.MAX_VALUE && distance[k][j] != Double.MAX_VALUE &&
                            distance[i][k] + distance[k][j] < distance[i][j]) {
                        distance[i][j] = distance[i][k] + distance[k][j];
                        next[i][j] = next[i][k];
                    }
                }
            }
        }

    }


    public double getDistance(int source, int destination) {
        return distance[source][destination];
    }

    public ArrayList<Integer> getPath() {
        return path;
    }



    public void PathConstruction (int src, int dst) {
        if (next[src][dst] != -1) {
            path.add(src);

            while (src != dst) {
                src = next[src][dst];
                path.add(src);
            }
        }
    }

    public void setPath(int source, int destination){
        path.clear();
        PathConstruction(source,destination);
    }

}