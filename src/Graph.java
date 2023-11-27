import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

public class Graph {
    int vertices;
    LinkedList<Edge>[] adjacencylist;
    ArrayList<Integer> path = new ArrayList<>();

    //Vytvoří graf s n vrcholama
    public Graph(int vertices) {
        this.vertices = vertices;
        adjacencylist = new LinkedList[vertices];
        //initialize adjacency lists for all the vertices
        for (int i = 0; i <vertices ; i++) {
            adjacencylist[i] = new LinkedList<>();
        }
    }

    //Přidání hran do grafu
    public void addEgde(int source, int destination, double weight) {
        Edge edge = new Edge(source, destination, weight);
        adjacencylist[source].addFirst(edge);

        edge = new Edge(destination, source, weight);
        adjacencylist[destination].addFirst(edge);
    }



    //Vytiskne graf
   /* public void printGraph(){
        for (int i = 0; i <vertices ; i++) {
            LinkedList<Edge> list = adjacencylist[i];
            for (int j = 0; j <list.size() ; j++) {
                System.out.println("vertex " + (i+1) + " is connected to " +
                        list.get(j).destination + " with weight " +  list.get(j).weight);
            }
        }
    }*/

    //Nejmenší vzdálenost od vrcholu k vrcholu
    public double dijkstraFromTo(int source, int destination) {
        double[] distance = new double[vertices]; // Pole pro uchování vzdáleností
        boolean[] visited = new boolean[vertices]; // Pole pro označení navštívených vrcholů
        int[] prev = new int[vertices]; // Pole pro uchování předchozího vrcholu na nejkratší cestě

        path.clear();
        Arrays.fill(distance, Double.MAX_VALUE); // Nastavení vzdálenosti na nekonečno
        distance[source] = 0; // Vzdálenost od zdrojového vrcholu je vždy 0

        for (int i = 0; i < vertices; i++) {
            int u = findMinDistance(distance, visited); // Najdi vrchol s nejmenší vzdáleností
            visited[u] = true; // Označ tento vrchol jako navštívený

            for (Edge edge : adjacencylist[u]) { // Projdi všechny hrany spojené s tímto vrcholem
                int v = edge.destination; // Získej druhý vrchol spojený touto hranou
                if (!visited[v] && distance[u] != Double.MAX_VALUE && distance[u] + edge.weight < distance[v]) {
                    distance[v] = distance[u] + edge.weight; // Pokud je nová vzdálenost menší, aktualizuj ji
                    prev[v] = u; // Uložíme předchozí vrchol na nejkratší cestě
                }
            }
        }

        for (int at = destination; at != 0; at = prev[at]) {
            path.add(at);
        }
        Collections.reverse(path);

        //printDijkstraResult(distance,source,destination, prev);
        return distance[destination];
    }


    private int findMinDistance(double[] distance, boolean[] visited) {
        double min = Double.MAX_VALUE;
        int minIndex = -1;

        for (int i = 0; i < vertices; i++) {
            if (!visited[i] && distance[i] <= min) {
                min = distance[i];
                minIndex = i;
            }
        }

        return minIndex;
    }

    public ArrayList<Integer> getPath(){
        return path;
    }

    private void printDijkstraResult(double[] distance, int source, int destination, int[] prev) {
        System.out.println("Nejkratší cesta z vrcholu " + (source) + " do vrcholu " + (destination) + " je: " + distance[destination] + " a vede přes tyto vrcholy: ");
        LinkedList<Integer> path = new LinkedList<>();
        int currentVertex = destination;

        while (currentVertex != source) {
            path.addFirst(currentVertex + 1);
            currentVertex = prev[currentVertex];
        }
        path.addFirst(source+1);

        for (int i = 0; i < path.size(); i++) {
            System.out.print(path.get(i));
            if (i != path.size() - 1) {
                System.out.print(" -> ");
            }
        }
        System.out.println();
    }
}
