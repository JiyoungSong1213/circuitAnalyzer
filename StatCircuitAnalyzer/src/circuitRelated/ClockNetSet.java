package circuitRelated;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClockNetSet {
	public Set<CircuitElement> allElementInClockNetSet;
	public List<ClockNet> clockNetSet;
	public String IPname; 
	
	public ClockNetSet() {
		allElementInClockNetSet = new HashSet<CircuitElement>();
	}
}
