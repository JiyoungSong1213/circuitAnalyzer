package circuitRelated;

import java.util.HashSet;
import java.util.Set;

public class ClockNet {
	public Set<CircuitElement> clockNetDIVandMUX; 
	public Set<CircuitElement> clockNetPLL;
	public Set<Connection> clockNetConnection;
	public CircuitElement keyElement;
	public String param = "";
	public boolean isUsed = true;
	public String usedTime = "00h 00m 00s 00ms";
	public double frequency = -1.0;
	public double cap;
	
	
	public ClockNet() {
		clockNetDIVandMUX = new HashSet<CircuitElement>();
		clockNetConnection = new HashSet<Connection>();
		clockNetPLL = new HashSet<CircuitElement>();
	}
}
