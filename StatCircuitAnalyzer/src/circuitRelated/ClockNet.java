package circuitRelated;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ClockNet {
	public Set<CircuitElement> clockNetDIVandMUX; 
	public Set<Connection> clockNetConnection;
	public CircuitElement keyElement;
	public String param;
//	public Set<ClockNet> prevClockNet;
//	public Set<ClockNet> nextClockNet;
//	public HashMap<Integer, Double> frequencyMap;
	public double capacity;
	public double frequency = 1.0;
	
	public ClockNet() {
		clockNetDIVandMUX = new HashSet<CircuitElement>();
//		prevClockNet = new HashSet<ClockNet>();
	}
}
