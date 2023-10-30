public abstract class ANode {
	double x;
	double y;

	static int count = 0;
	final int ORDER = ++count;
	
	
	public ANode(double x, double y) {
		this.x = x;
		this.y = y;
		
	}

}
