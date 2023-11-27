public class Borrow {

        String name;
        double speed;
        double currentDistance;
        double distance;
        int repairTime;
        int maxWeight;
        int currentbags;

        static int count = 0;
        final int ORDER = ++count;

        public Borrow(String name, double speed, double distance,int maxWeight, int repairTime) {
            this.name = name;
            this.speed = speed;
            this.distance = distance;
            this.repairTime = repairTime;
            this.maxWeight = maxWeight;
            currentbags = 0;
            currentDistance = distance;
        }

}
