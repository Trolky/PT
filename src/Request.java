public class Request {
	
	double warehouse;
	int costumer;
	int bag;
	int time;
	
	public Request(double warehouse, int costumer, int bag, int time) {
		super();
		this.warehouse = warehouse;
		this.costumer = costumer;
		this.bag = bag;
		this.time = time;
	}

	public double getWarehouse() {
		return warehouse;
	}

	public int getCostumer() {
		return costumer;
	}

	public int getBag() {
		return bag;
	}

	public int getTime() {
		return time;
	}
}
