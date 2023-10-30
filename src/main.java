import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class main {

    public static Customer[] customers;
    public static Warehouse[] warehouses;
    public static Path[] paths;
    public static ArrayList<Request> requests;
    public static BorrowType[] borrowsType;
    public static Graph graph;
    public static ArrayList<ANode> vertexs = new ArrayList<>();
    public static ArrayList<Borrow> borrows= new ArrayList<>();

    public static void main(String[] args) {
        int time =0;
        int count = 1;
        boolean requestStarted = false;
        Input in = new Input();
        in.read();
        load();
        createGraph();
        createBorrows();


        while(!requests.isEmpty()){
            int currentRequest = 0;//pokaždé bere request na indexu 0
            double distance = graph.dijkstraFromTo((int)requests.get(currentRequest).warehouse,requests.get(currentRequest).costumer); //vzdálenost k zákazníkovi

            int from = vertexs.get((int) requests.get(currentRequest).warehouse).ORDER; //Odkud
            int to = vertexs.get(requests.get(currentRequest).costumer).ORDER; //Kam


            Borrow selectedBorrow = new Borrow("",0,0,0,0);//inicializace kolečka s nulovýmy parametry

            //projde vytvořené kolečka a vybere to nejlepší. Nejdřív zkontroluje jestli se dokáže dostat tam i zpět a pak vybere ten s lepší rychlostí nebo s větší nostností.
            for(int borrow = 0;borrow<borrows.size();borrow++){
                if(borrows.get(borrow).distance>2*distance){
                    if(borrows.get(borrow).speed>selectedBorrow.speed||borrows.get(borrow).maxWeight>selectedBorrow.maxWeight){
                        selectedBorrow = borrows.get(borrow);
                    }
                }
            }

            int borrowOrder =selectedBorrow.ORDER;

            //pokud je kolečko naložené dovezeho k zákazníkovy.
            if(selectedBorrow.currentbags !=0){
                int bagsDelivered =  selectedBorrow.currentbags;
                selectedBorrow.currentbags -= bagsDelivered;
                requests.get(currentRequest).bag -=  bagsDelivered;

                //pokud je dovezen poslední pytel zákazníkovy kolečko se vrátí do skladu
                if(requests.get(currentRequest).bag == 0){
                    System.out.println("Cas: "+time+", Kolecko: "+borrowOrder+", Zakaznik:"+ to+ ", Vylozeno pytlu: "+bagsDelivered+ ", Casova rezerva: "+(requests.get(currentRequest).time-time));
                    requests.remove(currentRequest);
                    System.out.println("Cas: "+time+", Kolecko: "+borrowOrder+", Navrat do skladu: "+ from +"\n");
                    time += (int) (distance/selectedBorrow.speed)+borrowOrder;
                    count++;
                    requestStarted = false;
                    continue;
                }
                //jinak doveze pytel k zákazníkovy, vyloží pytle a vrátí se do skladu pro další.
                else {
                    System.out.println("Cas: " + time + ", Kolecko: " + borrowOrder + ", Zakaznik:" + to + ", Vylozeno pytlu: " + bagsDelivered + " , Vylozeno v: " + time+ ", Casova rezerva " +(requests.get(currentRequest).time-time));

                    System.out.println("Cas: "+time+", Kolecko: "+borrowOrder+", Navrat do skladu: "+ from);
                    time += (int) (distance/selectedBorrow.speed)+borrowOrder;
                    continue;
                }
            }

            //Podmínka pro vypsání požadavku jenom jednou
            if(!requestStarted) {
                System.out.println("Cas: " + time + ", Pozadavek: " + count + ", Zakaznik: " + to + ", Pocet pytlu " + requests.get(currentRequest).bag + ", Deadline " + requests.get(currentRequest).time);
                requestStarted = true;
            }

            //Naložení kolečka ve skladu
            System.out.println("Cas: "+time+", Kolecko: "+borrowOrder+ ", Sklad: " +from+ ", Nalozeno pytlu: "+selectedBorrow.maxWeight+ ", Odjezd: "+(time+borrowOrder));
            selectedBorrow.currentbags += selectedBorrow.maxWeight;

            time += (int) (distance/selectedBorrow.speed)+borrowOrder;
        }

        System.out.println("hotovo");

    }


    public static void createBorrows(){
        Random r = new Random();
        int borrowsPerFourPaths = paths.length/4;
        for(int i =0;i<borrowsType.length;i++){
            String name = borrowsType[i].name;
            double speed;
            double distance;
            if(borrowsType[i].minSpeed==borrowsType[i].maxSpeed){
                speed = borrowsType[i].minSpeed;
            }
            else{
                speed = r.nextDouble(borrowsType[i].minSpeed,borrowsType[i].maxSpeed);
            }
            if(borrowsType[i].minDist==borrowsType[i].maxDist){
                distance =  borrowsType[i].minDist;
            }
            else{
                distance = r.nextDouble(borrowsType[i].minDist,borrowsType[i].maxDist);
            }

            int weight = borrowsType[i].maxWeight;
            int repairTime = borrowsType[i].repairTime;
            for(int j = 0;j<borrowsPerFourPaths*borrowsType[i].proxy;j++){
                borrows.add(new Borrow(name,speed,distance,weight,repairTime));
            }
        }
    }

    public static void createGraph(){
        graph = new Graph(vertexs.size());
        for(int i = 0;i< paths.length;i++){
            graph.addEgde(paths[i].src,paths[i].dest,calculateWeight(paths[i]));
        }
    }

    public static double calculateWeight(Path path){
        ANode source = vertexs.get(path.src-1);
        ANode destination = vertexs.get(path.dest-1);

        return Math.sqrt(Math.pow(destination.x-source.x,2)+Math.pow(destination.y- source.y,2));
    }

    public static void printMinDistance(){
        for(int i = 0;i< requests.size();i++){
            System.out.println(graph.dijkstraFromTo((int) requests.get(i).warehouse,requests.get(i).costumer));
        }
    }


    public static void load() {
        try (BufferedReader reader = new BufferedReader(new FileReader("out.txt"))) {

            int nW = Integer.parseInt(reader.readLine());
            warehouses = new Warehouse[nW];
            for (int i = 0; i < nW; i++) {
                double x = Double.parseDouble(reader.readLine());
                double y = Double.parseDouble(reader.readLine());
                int bags = Integer.parseInt(reader.readLine());
                int reload = Integer.parseInt(reader.readLine());
                int load = Integer.parseInt(reader.readLine());
                vertexs.add(new Warehouse(x, y, bags, reload, load));
            }

            int nC = Integer.parseInt(reader.readLine());
            customers = new Customer[nC];
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
