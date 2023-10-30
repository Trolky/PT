import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class main {

  /*  public static Customer[] customers;
    public static Warehouse[] warehouses;*/
    public static Path[] paths;
    public static ArrayList<Request> requests;
    public static BorrowType[] borrowsType;
    public static ArrayList<ANode> vertexs = new ArrayList<>();

    public static void main(String[] args) {
        Input in = new Input();
        in.read();
        load();
        Simulation simulation = new Simulation(paths,requests,borrowsType,vertexs);
        simulation.simulationRun();


        System.out.println("hotovo");

    }





    /*public static void printMinDistance(){
        for(int i = 0;i< requests.size();i++){
            System.out.println(graph.dijkstraFromTo((int) requests.get(i).warehouse,requests.get(i).costumer));
        }
    }*/


    public static void load() {
        try (BufferedReader reader = new BufferedReader(new FileReader("out.txt"))) {

            int nW = Integer.parseInt(reader.readLine());
            //warehouses = new Warehouse[nW];
            for (int i = 0; i < nW; i++) {
                double x = Double.parseDouble(reader.readLine());
                double y = Double.parseDouble(reader.readLine());
                int bags = Integer.parseInt(reader.readLine());
                int reload = Integer.parseInt(reader.readLine());
                int load = Integer.parseInt(reader.readLine());
                vertexs.add(new Warehouse(x, y, bags, reload, load));
            }

            int nC = Integer.parseInt(reader.readLine());
           // customers = new Customer[nC];
            for (int i = 0; i < nC; i++) {
                double x = Double.parseDouble(reader.readLine());
                double y = Double.parseDouble(reader.readLine());
                vertexs.add(new Customer(x, y));
            }

            int nP = Integer.parseInt(reader.readLine());
            paths = new Path[nP];
            for (int i = 0; i < nP; i++) {
                int src = Integer.parseInt(reader.readLine());
                int dest = Integer.parseInt(reader.readLine());
                paths[i] = new Path(src, dest);
            }

            int nB = Integer.parseInt(reader.readLine());
            borrowsType = new BorrowType[nB];
            for (int i = 0; i < nB; i++) {
                String name = reader.readLine();
                double minSpeed = Double.parseDouble(reader.readLine());
                double maxSpeed = Double.parseDouble(reader.readLine());
                double minDist = Double.parseDouble(reader.readLine());
                double maxDist = Double.parseDouble(reader.readLine());
                int repairTime = Integer.parseInt(reader.readLine());
                int maxWeight = Integer.parseInt(reader.readLine());
                double proxy = Double.parseDouble(reader.readLine());
                borrowsType[i] = new BorrowType(name, minSpeed, maxSpeed, minDist, maxDist, repairTime, maxWeight, proxy);
            }

            int nR = Integer.parseInt(reader.readLine());
            requests = new ArrayList<>();
            for (int i = 0; i < nR; i++) {
                double warehouse = Double.parseDouble(reader.readLine());
                int costumer = Integer.parseInt(reader.readLine());
                int bag = Integer.parseInt(reader.readLine());
                int time = Integer.parseInt(reader.readLine());
                requests.add(new Request(warehouse, costumer, bag, time));
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
