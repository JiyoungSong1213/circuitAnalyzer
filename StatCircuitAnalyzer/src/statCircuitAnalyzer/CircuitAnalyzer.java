package statCircuitAnalyzer;

import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;

import plcopen.inf.type.IConnection;
import plcopen.inf.type.group.fbd.IInVariableInBlock;
import plcopen.inf.type.group.fbd.IOutVariable;
import plcopen.inf.type.group.fbd.IOutVariableInBlock;
import circuitRelated.CircuitElement;
import circuitRelated.CircuitInfo;
import circuitRelated.ClockNet;
import circuitRelated.ClockNetSet;
import circuitRelated.Connection;
import circuitRelated.CircuitElement;

public class CircuitAnalyzer {
	
//	static Element getElementByID (long LocalID) {
//		// Returns element that has Local ID as given LocalID.
//		for (Element elem : CircuitInfo.circuitElem) {
//			if (elem.LocalID == LocalID)
//				return elem;
//		}
//		return null;
//	}
//	
	static Connection getConnection(long prevLocalID, String prevParam, long nextLocalID, String nextParam) {
		for (Connection conn : CircuitInfo.connections) {
			if (conn.start == prevLocalID && conn.startParam.equals(prevParam) && conn.end == nextLocalID && conn.endParam.equals(nextParam))
				return conn;
		}
		return null;
	}
	
	public void makeClockNets () {
		for (CircuitElement IP : CircuitInfo.IPs) {
			ClockNet newClockNet = new ClockNet();
			newClockNet.keyElement = IP;
			newClockNet.clockNetConnection = makeClockNet(IP);
			CircuitInfo.clockNets.add(newClockNet);
		}
		for (CircuitElement block : CircuitInfo.blocks) {
			if (block.block.getTypeName().equals("DIV")) {
				ClockNet newClockNet = new ClockNet();	
				newClockNet.keyElement = block;
				newClockNet.clockNetConnection = makeClockNet(block);
				CircuitInfo.clockNets.add(newClockNet);
			}
			if (block.block.getTypeName().equals("MUX")) {
				for(IInVariableInBlock inVar : block.block.getInVariables()) {
					ClockNet newClockNet = new ClockNet();
					newClockNet.keyElement = block;
					newClockNet.param = inVar.getFormalParameter();
					newClockNet.clockNetConnection = makeClockNet(block, newClockNet.param);
					CircuitInfo.clockNets.add(newClockNet);
				}
			}
		}
	}
	
	public Set<Connection> makeClockNet (CircuitElement currentElem) {
		Set<Connection> currentElementConnectionSet = new HashSet<Connection>();
		
		if(currentElem.type==CircuitElement.BLOCK) {
			for(IInVariableInBlock inVar : currentElem.block.getInVariables()) {
				for (IConnection conn : inVar.getConnectionPointIn().getConnections()) {
					CircuitElement prevElement = CircuitInfo.getElementByID(conn.getRefLocalID());
					currentElementConnectionSet.add(getConnection(prevElement.LocalID, (prevElement.type == CircuitElement.BLOCK) ? (conn.getFormalParam())
							: (prevElement.invar.getExpression()), currentElem.LocalID, inVar.getFormalParameter()));
				}
			}
		} else if (currentElem.type == CircuitElement.OUTVAR) {
			for(IConnection conn : currentElem.outvar.getConnectionPointIn().getConnections()) {
				CircuitElement prevElement = CircuitInfo.getElementByID(conn.getRefLocalID());
				currentElementConnectionSet.add(getConnection(prevElement.LocalID, (prevElement.type == CircuitElement.BLOCK) ? (conn.getFormalParam())
						: (prevElement.invar.getExpression()), currentElem.LocalID, currentElem.outvar.getExpression()));
			}
		}
		
		if(currentElem.prevElement != null) {
			for (CircuitElement prevElem : currentElem.prevElement) {
				currentElementConnectionSet.addAll(makeClockNetForward(prevElem));
				if(prevElem.type == CircuitElement.BLOCK && prevElem.block.getTypeName().equals("DIV"))
					continue;
				if(prevElem.type == CircuitElement.BLOCK && prevElem.block.getTypeName().equals("MUX"))
					continue;
				if(prevElem.type == CircuitElement.INVAR)
					continue;
				currentElementConnectionSet.addAll(makeClockNet(prevElem));
			}
		}
		return currentElementConnectionSet;
	}
	
	public Set<Connection> makeClockNet (CircuitElement currentElem, String param) {
		Set<Connection> currentElementConnectionSet = new HashSet<Connection>();
		for(IInVariableInBlock inVar : currentElem.block.getInVariables()) {
			if (inVar.getFormalParameter().equals(param)){
				for (IConnection conn : inVar.getConnectionPointIn().getConnections()) {
					CircuitElement prevElement = CircuitInfo.getElementByID(conn.getRefLocalID());
					currentElementConnectionSet.add(getConnection(prevElement.LocalID, (prevElement.type == CircuitElement.BLOCK) ? (conn.getFormalParam())
							: (prevElement.invar.getExpression()), currentElem.LocalID, inVar.getFormalParameter()));
				
					currentElementConnectionSet.addAll(makeClockNetForward(prevElement));
					if(prevElement.type == CircuitElement.BLOCK && prevElement.block.getTypeName().equals("DIV"))
						continue;
					if(prevElement.type == CircuitElement.BLOCK && prevElement.block.getTypeName().equals("MUX"))
						continue;
					currentElementConnectionSet.addAll(makeClockNet(prevElement));
				}
			}
		}
		return currentElementConnectionSet;
	}
	
	public Set<Connection> makeClockNetForward (CircuitElement currentElem) {
		Set<Connection> currentElementConnectionSet = new HashSet<Connection>();
		if(currentElem.type == CircuitElement.BLOCK) {
			for(IOutVariableInBlock outVar : currentElem.block.getOutVariables()) {
				for (CircuitElement nextElem : currentElem.nextElement) {
					if (nextElem.type == CircuitElement.BLOCK) {
						for(IInVariableInBlock inVar : nextElem.block.getInVariables()) {
							Connection conn = getConnection(currentElem.LocalID, outVar.getFormalParameter(), nextElem.LocalID, inVar.getFormalParameter());
							if(conn != null) {
								currentElementConnectionSet.add(conn);
								if (nextElem.type == CircuitElement.BLOCK && nextElem.block.getTypeName().equals("DIV"))
									continue;
								if (nextElem.type == CircuitElement.BLOCK && nextElem.block.getTypeName().equals("MUX"))
									continue;
								if (nextElem.type == CircuitElement.INVAR)
									continue;
								currentElementConnectionSet.addAll(makeClockNetForward(nextElem));
							}
						}
					} else if (nextElem.type == CircuitElement.OUTVAR) {
						Connection conn = getConnection(currentElem.LocalID, outVar.getFormalParameter(), nextElem.LocalID, nextElem.outvar.getExpression());
						if(conn != null) {
							currentElementConnectionSet.add(conn);
						}
					}
				}
			}
		}
		return currentElementConnectionSet;
	}
	
	public Set<CircuitElement> getDIVandMUXInClockNet (ClockNet cn) {
		Set<CircuitElement> DIVandMUXSet = new HashSet<CircuitElement>();
		for(Connection conn : cn.clockNetConnection) {
			CircuitElement elem = CircuitInfo.getElementByID(conn.end);
			if (elem.type == CircuitElement.BLOCK && elem.block.getTypeName().equals("DIV"))
				DIVandMUXSet.add(elem);
			if (elem.type == CircuitElement.BLOCK && elem.block.getTypeName().equals("MUX"))
				DIVandMUXSet.add(elem);
		}
		return DIVandMUXSet;
	}
	
	public Set<ClockNet> clockNetSetForSelectedIP = new HashSet<ClockNet>();
	public ClockNet clockNetContainedSelectedIP;
	
	public void makeClockNetList (CircuitElement keyElem) {
		Scanner scanner = new Scanner(System.in);
		Set<ClockNet> cns = new HashSet<ClockNet>();
		cns = findClockNet(keyElem);
		for (ClockNet cn : cns){
			for(CircuitElement block : cn.clockNetDIVandMUX) {
				String selectedInput="";
				if (block.block.getTypeName().equals("MUX")) {
					System.out.println("Which input?");
					for(IInVariableInBlock inVar : block.block.getInVariables()) {
						System.out.print(inVar.getFormalParameter()+" ");
					}
					System.out.println();
					selectedInput = scanner.nextLine();
				}
				for(ClockNet clockNet : CircuitInfo.clockNets) {
					if(clockNet == cn)
						continue;
					if (clockNet.keyElement.LocalID == block.LocalID) {
						if (clockNet.keyElement.type == CircuitElement.BLOCK && clockNet.keyElement.block.getTypeName().equals("MUX") && selectedInput.equals(clockNet.param)) {
							clockNetSetForSelectedIP.add(clockNet);
							for(CircuitElement nextKeyElem : clockNet.clockNetDIVandMUX) {
								makeClockNetList (nextKeyElem);
							}
						} else if (clockNet.keyElement.type == CircuitElement.BLOCK && clockNet.keyElement.block.getTypeName().equals("DIV")) {
							clockNetSetForSelectedIP.add(clockNet);
							for(CircuitElement nextKeyElem : clockNet.clockNetDIVandMUX) {
								makeClockNetList (nextKeyElem);
							}
						} 
					}
				}
			}
		}
	}
	
	public Set<ClockNet> findClockNet (CircuitElement keyElem) {
		Set<ClockNet> clockNetSet = new HashSet<ClockNet>();
		for(ClockNet cn : CircuitInfo.clockNets) {
			if(cn.keyElement.LocalID == keyElem.LocalID)
				clockNetSet.add(cn);
		}
		return clockNetSet;
	}
	
	public void getFrequencies () {
		for (ClockNet cn : clockNetSetForSelectedIP) {
			Scanner scanner = new Scanner (System.in);
			System.out.println("What is frequency of clockNet started with " + cn.keyElement.LocalID + ", parameter: " + cn.param);
			cn.frequency = Double.parseDouble(scanner.nextLine());
		}
	}
	
	public void calculateCap (double totalPower, double voltage) {
		double power = 0;
		for (ClockNet cn : clockNetSetForSelectedIP) {
			power += (double) cn.clockNetConnection.size() * cn.frequency *cn.frequency *voltage;
		}
		double cap = totalPower / power;
		for (ClockNet cn : clockNetSetForSelectedIP) {
			cn.capacity = cn.clockNetConnection.size() * cap;
		}
	}
	
	
//	public void calculateFrequencies (ClockNet clockNet, double freq) {
//		clockNet.frequency *= freq;
//		for(Element keyElementOfClockNet : clockNet.clockNetDIVandMUX) {
//			for(ClockNet clockNetContainedKeyElem : findClockNet(keyElementOfClockNet)) {
//				if (clockNetSetForSelectedIP.contains(clockNetContainedKeyElem)) {
//					if (keyElementOfClockNet.block.getTypeName().equals("DIV")) {
//						StringTokenizer st = new StringTokenizer(keyElementOfClockNet.block.getInstanceName(), "/");
//						double numerator  = Double.parseDouble(st.nextToken());
//						double denominator = Double.parseDouble(st.nextToken());
////						double frequency = numerator/denominator;
//						
//						calculateFrequencies(clockNetContainedKeyElem, denominator);
//					} else if (keyElementOfClockNet.block.getTypeName().equals("MUX")) {
//						calculateFrequencies(clockNetContainedKeyElem, freq);
//					}
//				}
//			}
//		}
//	}
	
}
