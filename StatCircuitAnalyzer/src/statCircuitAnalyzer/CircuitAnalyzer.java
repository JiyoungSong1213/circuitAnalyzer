package statCircuitAnalyzer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import plcopen.inf.type.IConnection;
import plcopen.inf.type.group.fbd.IInVariableInBlock;
import circuitRelated.CircuitInfo;
import circuitRelated.ClockNet;
import circuitRelated.ClockNetSet;
import circuitRelated.Element;

public class CircuitAnalyzer {
	
//	public Set<Element> makeClockNetSet (Element currentElem) {
//		Set currentElementSet = new HashSet<Element>();
//		currentElementSet.add(currentElem);
//		if (currentElem.prevElement != null) {
//			for (Element e  : currentElem.prevElement){
//				makeClockNetSetForward(e);
//				currentElementSet.addAll(makeClockNetSet(e));
//				return currentElementSet;
//			}
//		}
//		return null;
//	}
	
//	public Set<Element> makeClockNetSetForward (Element currentElem) {
//		Set currentElementSet = new HashSet<Element>();
//		if(currentElem.nextElement != null) {
//			for(Element e : currentElem.nextElement) {
//				currentElementSet.add(e);
//				if (e.type == Element.BLOCK && e.block.getTypeName().equals("DIV"))
//					continue;
//				currentElementSet.addAll(makeClockNetSetForward(e));
//				return currentElementSet;
//			}
//		}
//		return null;
//	}
	
//	public void divideClockNetSet (ClockNetSet cns) {
//		ClockNet clockNet = new ClockNet();
//		for(Element e : cns.allElementInClockNetSet) {
//			if (e.type == Element.BLOCK && e.block.getTypeName().equals("DIV")) {
//				clockNet.clockNet = makeClockNetForward(e);
//			} else if (e.type == Element.BLOCK && e.block.getTypeName().equals("MUX")) {
//				clockNet.clockNet = makeClockNetForward(e);
//			} else if (e.type == Element.INVAR) {
//				clockNet.clockNet = makeClockNetForward(e);
//			}
//		}
//		CircuitInfo.clockNets.add(clockNet);
//	}
	
	
	public void makeClockNets () {
		for (Element IP : CircuitInfo.IPs) {
			ClockNet newClockNet = new ClockNet();
			newClockNet.keyElement = IP;
			newClockNet.clockNet = makeClockNet(IP);
			CircuitInfo.clockNets.add(newClockNet);
		}
		for (Element block : CircuitInfo.blocks) {
			if (block.block.getTypeName().equals("DIV")) {
				ClockNet newClockNet = new ClockNet();	
				newClockNet.keyElement = block;
				newClockNet.clockNet = makeClockNet(block);
				CircuitInfo.clockNets.add(newClockNet);
			}
			if (block.block.getTypeName().equals("MUX")) {
				for(IInVariableInBlock inVar : block.block.getInVariables()) {
					for (IConnection conn : inVar.getConnectionPointIn().getConnections()) {
						ClockNet newClockNet = new ClockNet();
						newClockNet.keyElement = block;
						newClockNet.param = conn.getFormalParam();
						newClockNet.clockNet = makeClockNet(block);
						CircuitInfo.clockNets.add(newClockNet);
						break;
					}
				}
			}
		}
	}
	
	public Set<Element> makeClockNet (Element currentElem) {
		Set currentElementSet = new HashSet<Element>();
		currentElementSet.add(currentElem);
		if (currentElem.prevElement != null) {
			for (Element e  : currentElem.prevElement){
				makeClockNetForward(e);
				if (e.type == Element.BLOCK && e.block.getTypeName().equals("DIV"))
					continue;
				if (e.type == Element.BLOCK && e.block.getTypeName().equals("MUX"))
					continue;
				currentElementSet.addAll(makeClockNet(e));
				return currentElementSet;
			}
		}
		return null;
	}
	
	public Set<Element> makeClockNetForward (Element currentElem) {
		Set currentElementSet = new HashSet<Element>();
		if(currentElem.nextElement != null) {
			for(Element e : currentElem.nextElement) {
				currentElementSet.add(e);
				if (e.type == Element.BLOCK && e.block.getTypeName().equals("DIV"))
					continue;
				if (e.type == Element.BLOCK && e.block.getTypeName().equals("MUX"))
					continue;
				currentElementSet.addAll(makeClockNetForward(e));
				return currentElementSet;
			}
		}
		return null;
	}
	
//	public boolean compareTwoSet (Set<Element> s1, Set<Element> s2) {
//		if(s1.size() != s2.size())
//			return false;
//		if(!s1.containsAll(s2))
//			return false;
//		return true;
//	}
	
	
	public void makeClockNetList (Element keyElem) {
		
		Set<ClockNet> cns = new HashSet<ClockNet>();
		cns = findClockNet(keyElem);
		for (ClockNet cn : cns){
			for(Element block : cn.clockNet) {
				if(block.LocalID == keyElem.LocalID)
					continue;
				if (block.type == Element.BLOCK && (block.block.getTypeName().equals("DIV") || block.block.getTypeName().equals("MUX"))) {
					for(ClockNet prevClockNet : findClockNet(block))
						cn.prevClockNet.add(prevClockNet);
					makeClockNetList(block);
				}
			}
		}
	}
	
	public Set<ClockNet> findClockNet (Element keyElem) {
		Set<ClockNet> clockNetSet = new HashSet<ClockNet>();
		for(ClockNet cn : CircuitInfo.clockNets) {
			if(cn.keyElement.LocalID == keyElem.LocalID)
				clockNetSet.add(cn);
		}
		return clockNetSet;
	}
	
}
