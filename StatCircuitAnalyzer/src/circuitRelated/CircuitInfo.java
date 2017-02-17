package circuitRelated;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CircuitInfo {

	public static List<Element> circuitElem = new ArrayList<Element>();
	public static List<Element> IPs;
	public static List<Element> blocks;
	public static List<Element> inputs;
	public static List<Connection> connections;
	public static List<ClockNetSet> clockNetSets;
	public static Set<ClockNet> clockNets;

	public static Element getElementByID(long LocalID) {
		for (Element elem : circuitElem) {
			if (elem.LocalID == LocalID)
				return elem;
		}
		return null;
	}
}
