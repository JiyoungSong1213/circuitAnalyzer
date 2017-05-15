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
		System.out.println("IP SIZE: "+ CircuitInfo.IPs.size());
		for (CircuitElement IP : CircuitInfo.IPs) {
			
			ClockNet newClockNet = new ClockNet();
			CircuitElement keyElement = new CircuitElement(IP.type, IP.LocalID);
			newClockNet.keyElement = keyElement;
			newClockNet.clockNetConnection = makeClockNet(IP);
//			System.out.println("clockNetConnection size: " + newClockNet.clockNetConnection.size());
			newClockNet.clockNetDIVandMUX = getDIVandMUXInClockNet(newClockNet);
//			System.out.println("DIVandMUXsize: "+ newClockNet.clockNetDIVandMUX);
			CircuitInfo.clockNets.add(newClockNet);
//			if(IP.outvar.getExpression().equals("CCLK_CA7_ABOX_TOP"))
//				for(Connection conn: newClockNet.clockNetConnection) {
//					System.out.println("CCLK: "+conn.start+" -- "+conn.end);
//				}
		}
//		System.out.println("block SIZE: "+ CircuitInfo.blocks.size());
		for (CircuitElement block : CircuitInfo.blocks) {
			if (block.block.getTypeName().equals("DIV") || block.block.getTypeName().equals("PLL")) {
				ClockNet newClockNet = new ClockNet();
				CircuitElement keyElement = new CircuitElement(block.type, block.LocalID);
				newClockNet.keyElement = keyElement;
				newClockNet.clockNetConnection = makeClockNet(block);
				newClockNet.clockNetDIVandMUX = getDIVandMUXInClockNet(newClockNet);
				CircuitInfo.clockNets.add(newClockNet);
			}
			if (block.block.getTypeName().equals("MUX2") || block.block.getTypeName().equals("MUX3")) {
				for(IInVariableInBlock inVar : block.block.getInVariables()) {
					ClockNet newClockNet = new ClockNet();
					CircuitElement keyElement = new CircuitElement(block.type, block.LocalID);
					newClockNet.keyElement = keyElement;
					newClockNet.param = inVar.getFormalParameter();
					newClockNet.clockNetConnection = makeClockNet(block, newClockNet.param);
					newClockNet.clockNetDIVandMUX = getDIVandMUXInClockNet(newClockNet);
					CircuitInfo.clockNets.add(newClockNet);
				}
			}
		}
	}
	
	public boolean compareConnection (Connection conn, Set<Connection> connSet) {
		for (Connection con : connSet) {
			if ((conn.end == con.end) && (conn.endParam.equals(con.endParam)) && (conn.start == con.start) && (conn.startParam.equals(con.startParam))) {
				return true;
			}
		}
		return false;
	}
	
	public Set<Connection> makeClockNet (CircuitElement currentElem) {
		Set<Connection> currentElementConnectionSet = new HashSet<Connection>();
		CircuitElement curElem = CircuitInfo.getElementByID(currentElem.LocalID);
//		System.out.println("prev elem size: "+ curElem.prevElement.size());
//		System.out.println("cur elem type: "+curElem.type);
//		if(curElem.block != null)
//		System.out.println("cur elem name: "+curElem.block.getTypeName());
		if(curElem.prevElement.size() != 0) {
			for (CircuitElement prevElem : curElem.prevElement) {
//				System.out.println("**prevElem name: "+ prevElem.block.getTypeName());
				Set<Connection> nextConns = makeClockNetForward(prevElem);
				for (Connection nextConn : nextConns) {
					if (currentElementConnectionSet.size() == 0)
						currentElementConnectionSet.add(nextConn);
					if (currentElementConnectionSet.size() != 0 && !compareConnection(nextConn, currentElementConnectionSet))
						currentElementConnectionSet.add(nextConn);
				}
				if(prevElem.type == CircuitElement.BLOCK && prevElem.block.getTypeName().equals("DIV"))
					continue;
				if(prevElem.type == CircuitElement.BLOCK && prevElem.block.getTypeName().equals("MUX2"))
					continue;
				if(prevElem.type == CircuitElement.BLOCK && prevElem.block.getTypeName().equals("MUX3"))
					continue;
				if(prevElem.type == CircuitElement.BLOCK && prevElem.block.getTypeName().equals("PLL"))
					continue;
				Set<Connection> prevConns = makeClockNet(prevElem);
				for (Connection prevConn : prevConns) {
					if (currentElementConnectionSet.size() == 0)
						currentElementConnectionSet.add(prevConn);
					if (currentElementConnectionSet.size() != 0 && !compareConnection(prevConn, currentElementConnectionSet))
						currentElementConnectionSet.add(prevConn);
				}
			}
		} else {
//			System.out.println("previous element is null!");
		}
	
		
		if(curElem.type==CircuitElement.BLOCK) {
			for(IInVariableInBlock inVar : curElem.block.getInVariables()) {
				for (IConnection conn : inVar.getConnectionPointIn().getConnections()) {
					CircuitElement prevElement = CircuitInfo.getElementByID(conn.getRefLocalID());
					Connection foundConnection = new Connection(prevElement.LocalID, (prevElement.type == CircuitElement.BLOCK) ? (conn.getFormalParam())
							: (prevElement.invar.getExpression()), curElem.LocalID, inVar.getFormalParameter());
//					System.out.println("&&prevElem name: "+prevElement.block.getTypeName());
					if (currentElementConnectionSet.size() == 0) {
						currentElementConnectionSet.add(foundConnection);
//						System.out.println("!!");
					} else if (currentElementConnectionSet.size() != 0 && !compareConnection(foundConnection, currentElementConnectionSet)) {
//						System.out.println("??");
						currentElementConnectionSet.add(foundConnection);
					}
//					if(prevElement.block.getTypeName().equals("MUX3"))
//						for(Connection ccc: currentElementConnectionSet){
//							System.out.println("!!!"+ccc.start);
//						}
					
				}
			}
		} else if (curElem.type == CircuitElement.OUTVAR) {
			for(IConnection conn : curElem.outvar.getConnectionPointIn().getConnections()) {
				CircuitElement prevElement = CircuitInfo.getElementByID(conn.getRefLocalID());
				Connection foundConnection = new Connection(prevElement.LocalID, (prevElement.type == CircuitElement.BLOCK) ? (conn.getFormalParam())
						: (prevElement.invar.getExpression()), curElem.LocalID, curElem.outvar.getExpression());
				if (currentElementConnectionSet.size() == 0) {
					currentElementConnectionSet.add(foundConnection);
				} else if (currentElementConnectionSet.size() != 0 && !compareConnection(foundConnection, currentElementConnectionSet)) {
					currentElementConnectionSet.add(foundConnection);
				}
			}
		}
//		System.out.println("curr connection size: "+currentElementConnectionSet.size());
		return currentElementConnectionSet;
	}
	
	public Set<Connection> makeClockNet (CircuitElement currentElem, String param) {
		Set<Connection> currentElementConnectionSet = new HashSet<Connection>();
		CircuitElement curElem = CircuitInfo.getElementByID(currentElem.LocalID);
		for(IInVariableInBlock inVar : curElem.block.getInVariables()) {
			if (inVar.getFormalParameter().equals(param)){
				for (IConnection conn : inVar.getConnectionPointIn().getConnections()) {
					CircuitElement prevElement = CircuitInfo.getElementByID(conn.getRefLocalID());
					currentElementConnectionSet.addAll(makeClockNetForward(prevElement));
					if(prevElement.type == CircuitElement.BLOCK && (prevElement.block.getTypeName().equals("DIV")
							|| prevElement.block.getTypeName().equals("PLL")))
						continue;
					if(prevElement.type == CircuitElement.BLOCK && (prevElement.block.getTypeName().equals("MUX2")
							|| prevElement.block.getTypeName().equals("MUX3")))
						continue;
					currentElementConnectionSet.addAll(makeClockNet(prevElement));
				}
				for (IConnection conn : inVar.getConnectionPointIn().getConnections()) {
					CircuitElement prevElement = CircuitInfo.getElementByID(conn.getRefLocalID());
					Connection foundConnection = new Connection(prevElement.LocalID, (prevElement.type == CircuitElement.BLOCK) ? (conn.getFormalParam())
							: (prevElement.invar.getExpression()), curElem.LocalID, inVar.getFormalParameter());
					if (currentElementConnectionSet.size() == 0) {
						currentElementConnectionSet.add(foundConnection);
					} else if (currentElementConnectionSet.size() != 0 && !compareConnection(foundConnection, currentElementConnectionSet)) {
						currentElementConnectionSet.add(foundConnection);
					}
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
								if (nextElem.type == CircuitElement.BLOCK && (nextElem.block.getTypeName().equals("DIV") || nextElem.block.getTypeName().equals("PLL")))
									continue;
								if (nextElem.type == CircuitElement.BLOCK && (nextElem.block.getTypeName().equals("MUX2") || nextElem.block.getTypeName().equals("MUX3")))
									continue;
								currentElementConnectionSet.addAll(makeClockNetForward(nextElem));
							}
						}
						for(IInVariableInBlock inVar : nextElem.block.getInVariables()) {
							Connection foundConnection = new Connection(currentElem.LocalID, outVar.getFormalParameter(), nextElem.LocalID, inVar.getFormalParameter());
							if (currentElementConnectionSet.size() == 0) {
								currentElementConnectionSet.add(foundConnection);
							} else if (currentElementConnectionSet.size() != 0 && !compareConnection(foundConnection, currentElementConnectionSet)) {
								currentElementConnectionSet.add(foundConnection);
							}
						}
					} else if (nextElem.type == CircuitElement.OUTVAR) {
						Connection foundConnection = new Connection(currentElem.LocalID, outVar.getFormalParameter(), nextElem.LocalID, nextElem.outvar.getExpression());
						if (currentElementConnectionSet.size() == 0) {
							currentElementConnectionSet.add(foundConnection);
						} else if (currentElementConnectionSet.size() != 0 && !compareConnection(foundConnection, currentElementConnectionSet)) {
							currentElementConnectionSet.add(foundConnection);
						}
					}
				}
			}
		}
		return currentElementConnectionSet;
	}
	
	public Set<CircuitElement> getDIVandMUXInClockNet (ClockNet cn) {
		Set<CircuitElement> DIVandMUXSet = new HashSet<CircuitElement>();
//		System.out.println("connection size: "+cn.clockNetConnection.size());
		for(Connection conn : cn.clockNetConnection) {
			CircuitElement elem = CircuitInfo.getElementByID(conn.start);
			if (elem.type == CircuitElement.BLOCK && (elem.block.getTypeName().equals("DIV") || elem.block.getTypeName().equals("PLL"))) {
				CircuitElement newElem = new CircuitElement(elem.type, elem.LocalID);
				DIVandMUXSet.add(newElem);
			} else if (elem.type == CircuitElement.BLOCK && (elem.block.getTypeName().equals("MUX2") || elem.block.getTypeName().equals("MUX3"))) {
				CircuitElement newElem = new CircuitElement(elem.type, elem.LocalID);
				DIVandMUXSet.add(newElem);
			}
		}
		return DIVandMUXSet;
	}
	
	public Set<ClockNet> clockNetSetForSelectedIP = new HashSet<ClockNet>();
	public ClockNet clockNetContainedSelectedIP;
	public double totalPower = 0;
	
	public double calcPower (double cap, double volt, double freq){
		return cap*volt*volt*freq;
	}
	
	public void makeClockNetList (CircuitElement keyElem) {
//		Scanner scanner = new Scanner(System.in);
//		Set<ClockNet> cns;
//		cns = findClockNet(keyElem);
//		System.out.println("CN SIZE: " + cns.size());
		
		if (keyElem.type == CircuitElement.BLOCK && (CircuitInfo.getElementByID(keyElem.LocalID).block.getTypeName().equals("MUX2")
				|| CircuitInfo.getElementByID(keyElem.LocalID).block.getTypeName().equals("MUX3"))) {
//			System.out.println("Which input?");
			String selectedInput ="IN1";// scanner.nextLine();
			for (ClockNet cn: CircuitInfo.clockNets) {
				if (cn.keyElement.LocalID == keyElem.LocalID && selectedInput.equals(cn.param)) {
//					System.out.println("clockNetDIVandMUXsize!!!: "+cn.clockNetDIVandMUX.size());
					ClockNet selectedClockNet = new ClockNet();
					selectedClockNet.keyElement = new CircuitElement(cn.keyElement.type, cn.keyElement.LocalID);
					selectedClockNet.param = cn.param;
					clockNetSetForSelectedIP.add(selectedClockNet);
//					System.out.println("MUX");
//					System.out.println("what is capacity, voltage, frequency of clock net with element" + cn.keyElement.LocalID);
//					String info = scanner.nextLine();
//					StringTokenizer st = new StringTokenizer(info," ");
					double cap = 50;//Double.parseDouble(st.nextToken());
					double volt = 3.5;//Double.parseDouble(st.nextToken());
					double freq = 500;//Double.parseDouble(st.nextToken());
					selectedClockNet.power = calcPower(cap, volt, freq);
					totalPower += selectedClockNet.power;
					for (CircuitElement nextKeyElem : cn.clockNetDIVandMUX) {
						boolean isInculded = false;
						for (ClockNet notDuplicateSelectedClockNet : clockNetSetForSelectedIP) {
							if (notDuplicateSelectedClockNet.keyElement.LocalID == nextKeyElem.LocalID)
								isInculded = true;
						}
						if (!isInculded)
							makeClockNetList(nextKeyElem);
					}
					break;
				}
			}
		} else {
			for (ClockNet cn: CircuitInfo.clockNets) {
				if(cn.keyElement.LocalID == keyElem.LocalID) {
//					System.out.println("clockNetDIVandMUXsize: "+cn.clockNetDIVandMUX.size());
//					for (CircuitElement nextKeyElem : cn.clockNetDIVandMUX)
//						makeClockNetList(nextKeyElem);
					ClockNet selectedClockNet = new ClockNet();
					selectedClockNet.keyElement = new CircuitElement(cn.keyElement.type, cn.keyElement.LocalID);
					selectedClockNet.param = cn.param;
					clockNetSetForSelectedIP.add(selectedClockNet);
					//System.out.println("OTHERS");
					//System.out.println("what is capacity, voltage, frequency of clock net with element" + cn.keyElement.LocalID);
					//String info = scanner.nextLine();
					//StringTokenizer st = new StringTokenizer(info," ");
					double cap = 50;//Double.parseDouble(st.nextToken());
					double volt = 3.5;//Double.parseDouble(st.nextToken());
					double freq = 0;//Double.parseDouble(st.nextToken());
					if(cn.keyElement.LocalID ==  18)
						freq = 26;
					else if (cn.keyElement.LocalID == 30)
						freq = 500;
					else if (cn.keyElement.LocalID == 103)
						freq = 500;
					selectedClockNet.power = calcPower(cap, volt, freq);
					totalPower += selectedClockNet.power;
					for (CircuitElement nextKeyElem : cn.clockNetDIVandMUX) {
						boolean isInculded = false;
						for (ClockNet notDuplicateSelectedClockNet : clockNetSetForSelectedIP) {
							if (notDuplicateSelectedClockNet.keyElement.LocalID == nextKeyElem.LocalID)
								isInculded = true;
						}
						if (!isInculded)
							makeClockNetList(nextKeyElem);
					}
					break;
				}
			}
		}
		
//		
//		for (ClockNet cn : cns){
//			System.out.println("block size: " + cn.clockNetDIVandMUX.size());
//			for(CircuitElement block : cn.clockNetDIVandMUX) {
//				
//				System.out.println("BLOCK");
//				String selectedInput="";
//				if (block.block.getTypeName().equals("MUX")) {
//					System.out.println("Which input?");
//					for(IInVariableInBlock inVar : block.block.getInVariables()) {
//						System.out.print(inVar.getFormalParameter()+" ");
//					}
//					System.out.println();
//					selectedInput = scanner.nextLine();
//				}
//				for(ClockNet clockNet : CircuitInfo.clockNets) {
//					if(clockNet == cn)
//						continue;
//					if (clockNet.keyElement.LocalID == block.LocalID) {
//						if (clockNet.keyElement.type == CircuitElement.BLOCK && clockNet.keyElement.block.getTypeName().equals("MUX") && selectedInput.equals(clockNet.param)) {
//							clockNetSetForSelectedIP.add(clockNet);
//							for(CircuitElement nextKeyElem : clockNet.clockNetDIVandMUX) {
//								makeClockNetList (nextKeyElem);
//							}
//						} else if (clockNet.keyElement.type == CircuitElement.BLOCK && clockNet.keyElement.block.getTypeName().equals("DIV")) {
//							clockNetSetForSelectedIP.add(clockNet);
//							for(CircuitElement nextKeyElem : clockNet.clockNetDIVandMUX) {
//								makeClockNetList (nextKeyElem);
//							}
//						} 
//					}
//				}
//			}
//		}
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
	
//	public void calculateCap (double totalPower, double voltage) {
//		double power = 0;
//		for (ClockNet cn : clockNetSetForSelectedIP) {
//			power += (double) cn.clockNetConnection.size() * cn.frequency *cn.frequency *voltage;
//		}
//		double cap = totalPower / power;
//		for (ClockNet cn : clockNetSetForSelectedIP) {
//			cn.capacity = cn.clockNetConnection.size() * cap;
//		}
//	}
	
	
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
