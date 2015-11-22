package tjjj.com.hackuci2015;

import java.util.LinkedList;

public class RunningQueue {
	
	private LinkedList<Double> ll;
	private int sizeThreshold;
	private double total;
	
	public RunningQueue(int newSizeThreshold) {
		ll = new LinkedList<Double>();
		sizeThreshold = newSizeThreshold;
	}
	
	public double insertAndRemoveElement(double d) {
		// Add element d
		insertElement(d);
		
		// Remove element from top if queue exceeds size threshold
		if (ll.size() <= sizeThreshold) {
			return 0;
		}
		return removeElement();
	}
	
	public void insertElement(double d) {
		ll.add(d);
		total = total + d;
	}
	
	public double removeElement() {
		double dToRemove = ll.remove();
		total = total - dToRemove;
		return dToRemove;
	}
	
	public double getFirst() {
		return ll.getFirst();
	}
	
	public double getLast() {
		return ll.getLast();
	}
	
	public double getSum() {
		return total;
	}
	
	public double getAvg() {
		return total / (double)ll.size();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		RunningQueue rq = new RunningQueue(5);
		for (int i = 0; i < 10; i++) {
			rq.insertAndRemoveElement((double)i);
		}
		System.out.println(rq.getFirst());
		System.out.println(rq.getLast());
		System.out.println(rq.getSum());
		System.out.println(rq.getAvg());
	}

}
