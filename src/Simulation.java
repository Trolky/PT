import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Simulation {
    public Path[] paths;
    public ArrayList<Request> requests;
    public BorrowType[] borrowsType;
    public Graph graph = null;
    public FloydGraph fgraph = null;
    public ArrayList<ANode> vertexs;
    public ArrayList<Borrow> borrows= new ArrayList<>();
    public int availableWarehouses;
    public double distance;
    public Simulation(Path[] paths, ArrayList<Request> requests, BorrowType[] borrowsType, ArrayList<ANode> vertexs){
        this.paths = paths;
        this.requests = requests;
        this.borrowsType = borrowsType;
        this.vertexs = vertexs;
        availableWarehouses = main.warehouseCount;
    }

    public void simulationRun(){
        if(availableWarehouses == 0){
            System.out.println("Ani jeden sklad nemá pytel");
        }

        else {
            if(Input.input.equals("middleM.txt")||Input.input.equals("middleL.txt")||Input.input.equals("denseL.txt")||Input.input.equals("denseH.txt")) createFloydGraph();
            else createGraph();
            createBorrows();
        }
        double time =0;
        int count = 1;
        boolean requestStarted = false;

        Borrow selectedBorrow = new Borrow("",0,0,0,0);
        Warehouse fromWarehouse = null;
        Collections.sort(requests, Comparator.comparingInt(Request::getTime));

        while(!requests.isEmpty() && availableWarehouses!=0){
            int currentRequest = 0;//pokaždé bere request na indexu 0

            int from = chooseWarehouse(requests.get(currentRequest).costumer); //Odkud
            int to = requests.get(currentRequest).costumer; //Kam

            //Podmínka pro vypsání požadavku jenom jednou
            if(!requestStarted) {
                if(time<requests.get(currentRequest).warehouse) time += requests.get(currentRequest).warehouse-time;
                System.out.println("Cas: " + time + ", Pozadavek: " + count + ", Zakaznik: " + to + ", Pocet pytlu " + requests.get(currentRequest).bag + ", Deadline " + requests.get(currentRequest).time);

                //projde vytvořené kolečka a vybere to nejlepší. Nejdřív zkontroluje jestli se dokáže dostat tam i zpět a pak vybere ten s lepší rychlostí nebo s větší nostností.
                for(int borrow = 0;borrow<borrows.size();borrow++){
                    if(borrows.get(borrow).distance>2*distance){
                        if(borrows.get(borrow).speed>selectedBorrow.speed||borrows.get(borrow).maxWeight>selectedBorrow.maxWeight){
                            selectedBorrow = borrows.get(borrow);
                        }
                    }
                }

                fromWarehouse = (Warehouse) vertexs.get(from);
                requestStarted = true;
            }

            from = vertexs.get(from).ORDER; //Odkud


            if(time < requests.get(currentRequest).warehouse){
                time += requests.get(currentRequest).warehouse - time;
            }


            //Kontrola jestli se požadavek vykonal v daném čase
            if(requests.get(currentRequest).time < time){
                System.out.println("Cas: " + time + ", Zakaznik: " + to + " umrzl zimou, protože jezdit s kolečkem je blbost \n");
                System.out.println("Zbytek zákazníků taky umrzl");
                requests.remove(currentRequest);
                break;
            }

            //Kontrola jestli existuje cesto od skladu k zákazníkovi
            if(distance == Double.MAX_VALUE){
                System.out.println("Cas: " + time + ", Pozadavek: " + count + " nelze splnit, protože neexistuje cesta od skladu "+from+" k zákazníkovi " + to+"\n");
                requests.remove(currentRequest);
                requestStarted = false;
                count++;
                continue;
            }


            int borrowOrder =selectedBorrow.ORDER;

            //Kontrola jestli se vybralo kolečko
            if(selectedBorrow.name.equals("")){
                System.out.println("Cas: " + time + ", Pozadavek: " + count + " nelze splnit, protože žádné kolečko nedojede ze skladu " + from + " k zákazníkovi " + to + " a zpět do skladu\n");
                requests.remove(currentRequest);
                requestStarted = false;
                count++;
                continue;
            }

            //Kontrola jestli aspoň jedno kolečko dojede k zákazníkovi
           if(selectedBorrow.currentDistance < distance){
               System.out.println("Cas :"+time+", Oprava kolecka: " + borrowOrder+", Opraveno v : "+(time+selectedBorrow.repairTime));
               selectedBorrow.currentDistance = selectedBorrow.distance;
               time += selectedBorrow.repairTime;
            }


            //pokud je kolečko naložené dovezeho k zákazníkovy.
            if(selectedBorrow.currentbags !=0){
                int bagsDelivered;

                if(selectedBorrow.maxWeight>requests.get(currentRequest).bag){
                    bagsDelivered = requests.get(currentRequest).bag;
                    selectedBorrow.currentbags -= bagsDelivered;
                    requests.get(currentRequest).bag = 0;
                }
                else{
                    bagsDelivered = selectedBorrow.maxWeight;
                    selectedBorrow.currentbags -= bagsDelivered;
                    requests.get(currentRequest).bag -= bagsDelivered;
                }

                //pokud je dovezen poslední pytel zákazníkovy kolečko se vrátí do skladu
                if(requests.get(currentRequest).bag == 0){

                    //vypíše hlášku když projede okolo vrcholu kde nic nevyloží
                    printVertexesBewteen(distance, time,borrowOrder, selectedBorrow.name);
                    time +=(distance/selectedBorrow.speed);

                    System.out.println("Cas: "+time+", Kolecko: "+borrowOrder+", Zakaznik: "+ to+ ", Vylozeno pytlu: "+bagsDelivered+", Vylozeno v: "+(time+bagsDelivered*fromWarehouse.load)+ ", Casova rezerva: "+(requests.get(currentRequest).time-time));
                    requests.remove(currentRequest);
                    time+=bagsDelivered*fromWarehouse.load;
                    System.out.println("Cas: "+time+", Kolecko: "+borrowOrder+", Navrat do skladu: "+ from);

                    //vypíše hlášku když projede okolo vrcholu kde nic nevyloží
                    printVertexesBewteen(distance,time,borrowOrder, selectedBorrow.name);System.out.println();
                    selectedBorrow.currentDistance -= distance;

                    time += (distance/selectedBorrow.speed);
                    count++;
                    requestStarted = false;
                    continue;
                }
                //jinak doveze pytel k zákazníkovy, vyloží pytle a vrátí se do skladu pro další.
                else {
                    //vypíše hlášku když projede okolo vrcholu kde nic nevyloží
                    printVertexesBewteen(distance,time,borrowOrder, selectedBorrow.name);

                    time += (distance/selectedBorrow.speed);
                    System.out.println("Cas: " + time + ", Kolecko: " + borrowOrder + ", Zakaznik: " + to + ", Vylozeno pytlu: " + bagsDelivered + " , Vylozeno v: " + (time+bagsDelivered*fromWarehouse.load)+ ", Casova rezerva " +(requests.get(currentRequest).time-time));
                    time+=bagsDelivered*fromWarehouse.load;

                    System.out.println("Cas: "+time+", Kolecko: "+borrowOrder+", Navrat do skladu: "+ from);
                    time += (distance/selectedBorrow.speed);

                    //vypíše hlášku když projede okolo vrcholu kde nic nevyloží
                    printVertexesBewteen(distance,time,borrowOrder, selectedBorrow.name);
                    selectedBorrow.currentDistance -= distance;

                    continue;
                }
            }

            //kontrola jestli sklad má nějaké pytle
            if(fromWarehouse.currentBags == 0){
                fromWarehouse.currentBags += fromWarehouse.bags;
                time += fromWarehouse.reload;
                continue;
            }

            //Naložení kolečka ve skladu
            int loadedBags;
            if(fromWarehouse.currentBags >= selectedBorrow.maxWeight){
                selectedBorrow.currentbags = selectedBorrow.maxWeight;
                loadedBags = selectedBorrow.maxWeight;
            }
            else{
                selectedBorrow.currentbags += fromWarehouse.currentBags;
                loadedBags = fromWarehouse.currentBags;
            }
            fromWarehouse.currentBags -= loadedBags;
            if(fromWarehouse.currentBags == 0){
                fromWarehouse.currentBags = fromWarehouse.bags;
                time += fromWarehouse.reload;
            }
            System.out.println("Cas: "+time+", Kolecko: "+borrowOrder+ ", Sklad: " +from+ ", Nalozeno pytlu: "+loadedBags+ ", Odjezd: "+(time+loadedBags*fromWarehouse.load));
            time += loadedBags*fromWarehouse.load;

        }


    }

    public void createBorrows(){
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

    //Vybere nejbližší sklad pro daný požadavek
    public int chooseWarehouse(int to){
       int index = 0;
       double min_distance = Double.MAX_VALUE;
       double distance;

        for(int i = 0;i<availableWarehouses;i++){
            if(graph!=null) {
                if (availableWarehouses > 1) distance = graph.dijkstraFromTo(i, (availableWarehouses + to) - 1);
                else distance = graph.dijkstraFromTo(i, to);
            }
            else{
                if (availableWarehouses > 1) distance = fgraph.getDistance(i, (availableWarehouses + to) - 1);
                else distance = fgraph.getDistance(i, to);
            }

            if(distance != Double.MAX_VALUE && distance<min_distance){
                min_distance = distance;
                index = i;
            }
        }

        if(fgraph!= null) {
            fgraph.setPath(index, (availableWarehouses + to) - 1);
        }

        this.distance = min_distance;
        return index;
    }

    public void createGraph(){
        graph = new Graph(vertexs.size());
        for(int i = 0;i< paths.length;i++){
            int from = paths[i].src-1;
            int to = paths[i].dest-1;
            graph.addEgde(from,to,calculateWeight(paths[i]));
        }
    }

    public void createFloydGraph(){
        fgraph = new FloydGraph(vertexs.size());
        for(int i = 0;i< paths.length;i++){
            int from = paths[i].src-1;
            int to = paths[i].dest-1;
            fgraph.addEdge(from,to,calculateWeight(paths[i]));
        }
        fgraph.floydWarshall();
        System.out.println("DASD");
    }

    public void printVertexesBewteen(double distance, double time,int borrowOrder, String type){
        ArrayList<Integer> path;
        Locale.setDefault(Locale.ENGLISH);
        DecimalFormat df=new DecimalFormat("#.#");
        if(graph!=null) {
            path = graph.getPath();
            if(path.size()>1) {
                for (int i = 0; i < path.size() - 1; i++) {
                    time += (distance/path.size())*(i+1);
                    System.out.println("Cas: " + df.format(time) + ", Kolecko: " + borrowOrder + ", Zakaznik: " + path.get(i) + ", kuk na " + type + " kolecko");
                }
            }
        }
        else{
            path = fgraph.getPath();
            if(path.size()>2) {
                for (int i = 1; i < path.size() - 1; i++) {
                    time += (distance/path.size())*(i+1);
                    System.out.println("Cas: " + df.format(time) + ", Kolecko: " + borrowOrder + ", Zakaznik: " + path.get(i) + ", kuk na " + type + " kolecko");
                }
            }
        }
    }

    public double calculateWeight(Path path){
        ANode source = vertexs.get(path.src-1);
        ANode destination = vertexs.get(path.dest-1);

        return Math.sqrt(Math.pow(destination.x-source.x,2)+Math.pow(destination.y- source.y,2));
    }
}
