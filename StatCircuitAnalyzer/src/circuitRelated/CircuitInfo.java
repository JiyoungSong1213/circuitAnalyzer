package circuitRelated;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CircuitInfo {

	public static List<CircuitElement> circuitElem = new ArrayList<CircuitElement>();
	public static List<CircuitElement> IPs = new ArrayList<CircuitElement>();
	public static List<CircuitElement> blocks = new ArrayList<CircuitElement>();
	public static List<CircuitElement> inputs = new ArrayList<CircuitElement>();
	public static List<Connection> connections = new ArrayList<Connection>();
	public static List<ClockNetSet> clockNetSets = new ArrayList<ClockNetSet>();
	public static Set<ClockNet> clockNets = new HashSet<ClockNet>();
	public static List<InVariable> inVariables = new ArrayList<InVariable>();
	public static List<OutVariable> outVariables = new ArrayList<OutVariable>();
	public static List<Block> functions = new ArrayList<Block>();

	public static CircuitElement getElementByID(long LocalID) {
		for (CircuitElement elem : circuitElem) {
			if (elem.LocalID == LocalID)
				return elem;
		}
		return null;
	}
	
	public static CircuitElement getIPByName (String IPname) {
		for (CircuitElement IP : IPs) {
			if (IP.outvar.getExpression().equals(IPname))
				return IP;
		}
		return null;
	}
}
