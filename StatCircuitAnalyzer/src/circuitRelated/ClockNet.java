package circuitRelated;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ClockNet {
	public Set<Element> clockNet; 
	public Set<Connection> clockNetConnection;
	public Element keyElement;
	public Set<ClockNet> prevClockNet;
	public HashMap<Integer, Double> frequencyMap;
	public ClockNet() {
		clockNet = new HashSet<Element>();
		prevClockNet = new HashSet<ClockNet>();
	}
}
