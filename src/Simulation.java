import java.util.ArrayList;
import java.util.Random;

public class Simulation {
    public Path[] paths;
    public ArrayList<Request> requests;
    public BorrowType[] borrowsType;
    public Graph graph;
    public ArrayList<ANode> vertexs;
    public ArrayList<Borrow> borrows= new ArrayList<>();


    public Simulation(Path[] paths, ArrayList<Request> requests, BorrowType[] borrowsType, ArrayList<ANode> vertexs){
        this.paths = paths;
        this.requests = requests;
        this.borrowsType = borrowsType;
        this.vertexs = vertexs;
    }

    public void simulationRun(){
        createGraph();
        createBorrows();

        int time =0;
        int count = 1;
        boolean requestStarted = false;


        while(!requests.isEmpty()){
            int currentRequest = 0;//pokaždé bere request na indexu 0
            double distance = graph.dijkstraFromTo((int)Math.round(requests.get(currentRequest).warehouse),requests.get(currentRequest).costumer); //vzdálenost k zákazníkovi

            int from = vertexs.get((int) requests.get(currentRequest).warehouse).ORDER; //Odkud
            int to = vertexs.get(requests.get(currentRequest).costumer).ORDER; //Kam

            //Podmínka pro vypsání požadavku jenom jednou
            if(!requestStarted) {
                System.out.println("Cas: " + time + ", Pozadavek: " + count + ", Zakaznik: " + to + ", Pocet pytlu " + requests.get(currentRequest).bag + ", Deadline " + requests.get(currentRequest).time);
                requestStarted = true;
            }

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

            //Kontrola jestli aspoň jedno kolečko dojede k zákazníkovi
            if(selectedBorrow.distance<distance){
                System.out.println("Cas: " + time + ", Pozadavek: " + count + " nelze splnit, protože žádné kolečko nedojede ze skladu "+from+" k zákazníkovi " + to+" a zpět do skladu");
                requests.remove(currentRequest);
                count++;
                continue;
            }
            //pokud je kolečko naložené dovezeho k zákazníkovy.
            if(selectedBorrow.currentbags !=0){
                int bagsDelivered;

                if(selectedBorrow.maxWeight>requests.get(currentRequest).bag){
                    bagsDelivered = requests.get(currentRequest).bag;
                    selectedBorrow.currentbags -= requests.get(currentRequest).bag;
                    requests.get(currentRequest).bag = 0;
                }
                else{
                    bagsDelivered = selectedBorrow.maxWeight;
                    selectedBorrow.currentbags -= bagsDelivered;
                    requests.get(currentRequest).bag -= bagsDelivered;
                }

                if(selectedBorrow.currentbags >= requests.get(currentRequest).bag){
                    time += (int) (distance/selectedBorrow.speed)+borrowOrder;
                }

                //pokud je dovezen poslední pytel zákazníkovy kolečko se vrátí do skladu
                if(requests.get(currentRequest).bag == 0){
                    System.out.println("Cas: "+time+", Kolecko: "+borrowOrder+", Zakaznik: "+ to+ ", Vylozeno pytlu: "+bagsDelivered+ ", Casova rezerva: "+(requests.get(currentRequest).time-time));
                    requests.remove(currentRequest);
                    System.out.println("Cas: "+time+", Kolecko: "+borrowOrder+", Navrat do skladu: "+ from +"\n");
                    time += (int) (distance/selectedBorrow.speed)+borrowOrder;
                    count++;
                    requestStarted = false;
                    continue;
                }
                //jinak doveze pytel k zákazníkovy, vyloží pytle a vrátí se do skladu pro další.
                else {
                    System.out.println("Cas: " + time + ", Kolecko: " + borrowOrder + ", Zakaznik: " + to + ", Vylozeno pytlu: " + bagsDelivered + " , Vylozeno v: " + time+ ", Casova rezerva " +(requests.get(currentRequest).time-time));

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
    }

    public void createBorrows(){
        Random r = new Random();
        int borrowsPerFourPaths = paths.length/8;
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

    public void createGraph(){
        graph = new Graph(vertexs.size());
        for(int i = 0;i< paths.length;i++){
            int from = paths[i].src-1;
            int to = paths[i].dest-1;
            graph.addEgde(from,to,calculateWeight(paths[i]));
        }
    }

    public double calculateWeight(Path path){
        ANode source = vertexs.get(path.src-1);
        ANode destination = vertexs.get(path.dest-1);

        return Math.sqrt(Math.pow(destination.x-source.x,2)+Math.pow(destination.y- source.y,2));
    }
}
