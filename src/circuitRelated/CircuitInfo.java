package circuitRelated;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CircuitInfo {
	
	public String circuitName = "";
	public List<CircuitElement> circuitElem = new ArrayList<CircuitElement>();
	public List<CircuitElement> IPs = new ArrayList<CircuitElement>();
	public List<CircuitElement> blocks = new ArrayList<CircuitElement>();
	public List<CircuitElement> inputs = new ArrayList<CircuitElement>();
	public List<Connection> connections = new ArrayList<Connection>();
	public Set<ClockNet> clockNets = new HashSet<ClockNet>();
	public CircuitFreqUsageMapInfo mapList = new CircuitFreqUsageMapInfo();
	//mapping-related class list

	public static CircuitElement getElementByID(long LocalID, List<CircuitElement> circuitElem) {
		for (CircuitElement elem : circuitElem) {
			if (elem.LocalID == LocalID)
				return elem;
		}
		return null;
	}
	
	public static CircuitElement getIPByName (String IPname, List<CircuitElement> IPs) {
		for (CircuitElement IP : IPs) {
			if (IP.type == CircuitElement.OUTVAR && IP.outvar.getExpression().equals(IPname))
				return IP;
		}
		return null;
	}
	
	public static double getFreqByID(long localID, CircuitInfo ci) {
		for (CircuitFreqInfo cfi: ci.mapList.CFIs) {
			if (cfi.localID == localID)
				return cfi.value;
		}
		return 0;
	}
}
