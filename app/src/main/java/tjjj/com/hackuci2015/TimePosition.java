package tjjj.com.hackuci2015;

public class TimePosition {
	
	private int start;
	private int end;
	
	public TimePosition(int newStart, int newEnd) {
		start = newStart;
		end = newEnd;
	}
	
	public int getStart() {
		return start;
	}
	
	public int getEnd() {
		return end;
	}
	
	public String printTime() {
		return "Start: " + start + ", End: " + end;
	}

}
