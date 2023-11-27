public class Warehouse extends ANode {
	
	int bags;
	int reload;
	int load;
	int currentBags;
	

	public Warehouse(double x, double y, int bags, int reload, int load) {
		super(x, y);
		this.bags = bags;
		this.reload = reload;
		this.load = load;
		currentBags = bags;
	}
	
	
	
	@Override 
	public String toString() {
		return "Sklad: "+ this.ORDER;
		
	}

}
