public class BorrowType {
	
	String name;
	double minSpeed;
	double maxSpeed;
	double minDist;
	double maxDist;
	int repairTime;
	int maxWeight;
	double proxy;
	
	public BorrowType(String name, double minSpeed, double maxSpeed, double minDist, double maxDist, int repairTime, int maxWeight, double proxy) {
		this.name = name;
		this.minSpeed = minSpeed;
		this.maxSpeed = maxSpeed;
		this.minDist = minDist;
		this.maxDist = maxDist;
		this.repairTime = repairTime;
		this.maxWeight = maxWeight;
		this.proxy = proxy;
	}
	
	
}
