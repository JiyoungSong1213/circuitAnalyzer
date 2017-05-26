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
import circuitRelated.CircuitFreqInfo;
import circuitRelated.CircuitInfo;
import circuitRelated.CircuitPLLInfo;
import circuitRelated.CircuitUsageMapInfo;
import circuitRelated.ClockNet;
import circuitRelated.ClockNetSet;
import circuitRelated.Connection;
import circuitRelated.CircuitElement;

public class CircuitAnalyzer {

	// static Element getElementByID (long LocalID) {
	// // Returns element that has Local ID as given LocalID.
	// for (Element elem : CircuitInfo.circuitElem) {
	// if (elem.LocalID == LocalID)
	// return elem;
	// }
	// return null;
	// }
	//
	static Connection getConnection(long prevLocalID, String prevParam, long nextLocalID, String nextParam,
			CircuitInfo ci) {
		for (Connection conn : ci.connections) {
			if (conn.start == prevLocalID && conn.startParam.equals(prevParam) && conn.end == nextLocalID
					&& conn.endParam.equals(nextParam))
				return conn;
		}
		return null;
	}

	public void makeClockNets(CircuitInfo ci) {
		System.out.println("IP SIZE: " + ci.IPs.size());
		for (CircuitElement IP : ci.IPs) {
			ClockNet newClockNet = new ClockNet();
			CircuitElement keyElement = new CircuitElement(IP.type, IP.LocalID);
			newClockNet.keyElement = keyElement;
			newClockNet.clockNetConnection = makeClockNet(IP, ci);
			newClockNet.clockNetDIVandMUX = getDIVandMUXInClockNet(newClockNet, ci);
			newClockNet.clockNetPLL = getPLLInClockNet(newClockNet, ci);
			ci.clockNets.add(newClockNet);
		}
		for (CircuitElement block : ci.blocks) {
			if (block.block.getTypeName().equals("DIV") || block.block.getTypeName().equals("PLL")) {
				ClockNet newClockNet = new ClockNet();
				CircuitElement keyElement = new CircuitElement(block.type, block.LocalID);
				newClockNet.keyElement = keyElement;
				newClockNet.clockNetConnection = makeClockNet(block, ci);
				newClockNet.clockNetDIVandMUX = getDIVandMUXInClockNet(newClockNet, ci);
				ci.clockNets.add(newClockNet);
			}
			if (block.block.getTypeName().equals("MUX2") || block.block.getTypeName().equals("MUX3")) {
				for (IInVariableInBlock inVar : block.block.getInVariables()) {
					ClockNet newClockNet = new ClockNet();
					CircuitElement keyElement = new CircuitElement(block.type, block.LocalID);
					newClockNet.keyElement = keyElement;
					newClockNet.param = inVar.getFormalParameter();
					newClockNet.clockNetConnection = makeClockNet(block, newClockNet.param, ci);
					newClockNet.clockNetDIVandMUX = getDIVandMUXInClockNet(newClockNet, ci);
					ci.clockNets.add(newClockNet);
				}
			}
		}
	}

	public boolean compareConnection(Connection conn, Set<Connection> connSet) {
		for (Connection con : connSet) {
			if ((conn.end == con.end) && (conn.endParam.equals(con.endParam)) && (conn.start == con.start)
					&& (conn.startParam.equals(con.startParam))) {
				return true;
			}
		}
		return false;
	}

	public Set<Connection> makeClockNet(CircuitElement currentElem, CircuitInfo ci) {
		Set<Connection> currentElementConnectionSet = new HashSet<Connection>();
		CircuitElement curElem = CircuitInfo.getElementByID(currentElem.LocalID, ci.circuitElem);
		// System.out.println("prev elem size: "+ curElem.prevElement.size());
		// System.out.println("cur elem type: "+curElem.type);
		// if(curElem.block != null)
		// System.out.println("cur elem name: "+curElem.block.getTypeName());
		if (curElem.prevElement.size() != 0) {
			for (CircuitElement prevElem : curElem.prevElement) {
				// System.out.println("**prevElem name: "+
				// prevElem.block.getTypeName());
				Set<Connection> nextConns = makeClockNetForward(prevElem, ci);
				for (Connection nextConn : nextConns) {
					if (currentElementConnectionSet.size() == 0)
						currentElementConnectionSet.add(nextConn);
					if (currentElementConnectionSet.size() != 0
							&& !compareConnection(nextConn, currentElementConnectionSet))
						currentElementConnectionSet.add(nextConn);
				}
				if (prevElem.type == CircuitElement.BLOCK && prevElem.block.getTypeName().equals("DIV"))
					continue;
				if (prevElem.type == CircuitElement.BLOCK && prevElem.block.getTypeName().equals("MUX2"))
					continue;
				if (prevElem.type == CircuitElement.BLOCK && prevElem.block.getTypeName().equals("MUX3"))
					continue;
				if (prevElem.type == CircuitElement.BLOCK && prevElem.block.getTypeName().equals("PLL"))
					continue;
				Set<Connection> prevConns = makeClockNet(prevElem, ci);
				for (Connection prevConn : prevConns) {
					if (currentElementConnectionSet.size() == 0)
						currentElementConnectionSet.add(prevConn);
					if (currentElementConnectionSet.size() != 0
							&& !compareConnection(prevConn, currentElementConnectionSet))
						currentElementConnectionSet.add(prevConn);
				}
			}
		} else {
			// System.out.println("previous element is null!");
		}

		if (curElem.type == CircuitElement.BLOCK) {
			for (IInVariableInBlock inVar : curElem.block.getInVariables()) {
				for (IConnection conn : inVar.getConnectionPointIn().getConnections()) {
					CircuitElement prevElement = CircuitInfo.getElementByID(conn.getRefLocalID(), ci.circuitElem);
					Connection foundConnection = new Connection(prevElement.LocalID,
							(prevElement.type == CircuitElement.BLOCK) ? (conn.getFormalParam())
									: (prevElement.invar.getExpression()),
							curElem.LocalID, inVar.getFormalParameter());
					// System.out.println("&&prevElem name:
					// "+prevElement.block.getTypeName());
					if (currentElementConnectionSet.size() == 0) {
						currentElementConnectionSet.add(foundConnection);
						// System.out.println("!!");
					} else if (currentElementConnectionSet.size() != 0
							&& !compareConnection(foundConnection, currentElementConnectionSet)) {
						// System.out.println("??");
						currentElementConnectionSet.add(foundConnection);
					}
					// if(prevElement.block.getTypeName().equals("MUX3"))
					// for(Connection ccc: currentElementConnectionSet){
					// System.out.println("!!!"+ccc.start);
					// }

				}
			}
		} else if (curElem.type == CircuitElement.OUTVAR) {
			for (IConnection conn : curElem.outvar.getConnectionPointIn().getConnections()) {
				CircuitElement prevElement = CircuitInfo.getElementByID(conn.getRefLocalID(), ci.circuitElem);
				Connection foundConnection = new Connection(prevElement.LocalID,
						(prevElement.type == CircuitElement.BLOCK) ? (conn.getFormalParam())
								: (prevElement.invar.getExpression()),
						curElem.LocalID, curElem.outvar.getExpression());
				if (currentElementConnectionSet.size() == 0) {
					currentElementConnectionSet.add(foundConnection);
				} else if (currentElementConnectionSet.size() != 0
						&& !compareConnection(foundConnection, currentElementConnectionSet)) {
					currentElementConnectionSet.add(foundConnection);
				}
			}
		}
		// System.out.println("curr connection size:
		// "+currentElementConnectionSet.size());
		return currentElementConnectionSet;
	}

	public Set<Connection> makeClockNet(CircuitElement currentElem, String param, CircuitInfo ci) {
		Set<Connection> currentElementConnectionSet = new HashSet<Connection>();
		CircuitElement curElem = CircuitInfo.getElementByID(currentElem.LocalID, ci.circuitElem);
		for (IInVariableInBlock inVar : curElem.block.getInVariables()) {
			if (inVar.getFormalParameter().equals(param)) {
				for (IConnection conn : inVar.getConnectionPointIn().getConnections()) {
					CircuitElement prevElement = CircuitInfo.getElementByID(conn.getRefLocalID(), ci.circuitElem);
					currentElementConnectionSet.addAll(makeClockNetForward(prevElement, ci));
					if (prevElement.type == CircuitElement.BLOCK && (prevElement.block.getTypeName().equals("DIV")
							|| prevElement.block.getTypeName().equals("PLL")))
						continue;
					if (prevElement.type == CircuitElement.BLOCK && (prevElement.block.getTypeName().equals("MUX2")
							|| prevElement.block.getTypeName().equals("MUX3")))
						continue;
					currentElementConnectionSet.addAll(makeClockNet(prevElement, ci));
				}
				for (IConnection conn : inVar.getConnectionPointIn().getConnections()) {
					CircuitElement prevElement = CircuitInfo.getElementByID(conn.getRefLocalID(), ci.circuitElem);
					Connection foundConnection = new Connection(prevElement.LocalID,
							(prevElement.type == CircuitElement.BLOCK) ? (conn.getFormalParam())
									: (prevElement.invar.getExpression()),
							curElem.LocalID, inVar.getFormalParameter());
					if (currentElementConnectionSet.size() == 0) {
						currentElementConnectionSet.add(foundConnection);
					} else if (currentElementConnectionSet.size() != 0
							&& !compareConnection(foundConnection, currentElementConnectionSet)) {
						currentElementConnectionSet.add(foundConnection);
					}
				}
			}
		}
		return currentElementConnectionSet;
	}

	public Set<Connection> makeClockNetForward(CircuitElement currentElem, CircuitInfo ci) {
		Set<Connection> currentElementConnectionSet = new HashSet<Connection>();
		if (currentElem.type == CircuitElement.BLOCK) {
			for (IOutVariableInBlock outVar : currentElem.block.getOutVariables()) {
				for (CircuitElement nextElem : currentElem.nextElement) {
					if (nextElem.type == CircuitElement.BLOCK) {
						for (IInVariableInBlock inVar : nextElem.block.getInVariables()) {
							Connection conn = getConnection(currentElem.LocalID, outVar.getFormalParameter(),
									nextElem.LocalID, inVar.getFormalParameter(), ci);
							if (conn != null) {
								if (nextElem.type == CircuitElement.BLOCK && (nextElem.block.getTypeName().equals("DIV")
										|| nextElem.block.getTypeName().equals("PLL")))
									continue;
								if (nextElem.type == CircuitElement.BLOCK
										&& (nextElem.block.getTypeName().equals("MUX2")
												|| nextElem.block.getTypeName().equals("MUX3")))
									continue;
								currentElementConnectionSet.addAll(makeClockNetForward(nextElem, ci));
							}
						}
						for (IInVariableInBlock inVar : nextElem.block.getInVariables()) {
							Connection foundConnection = new Connection(currentElem.LocalID,
									outVar.getFormalParameter(), nextElem.LocalID, inVar.getFormalParameter());
							if (currentElementConnectionSet.size() == 0) {
								currentElementConnectionSet.add(foundConnection);
							} else if (currentElementConnectionSet.size() != 0
									&& !compareConnection(foundConnection, currentElementConnectionSet)) {
								currentElementConnectionSet.add(foundConnection);
							}
						}
					} else if (nextElem.type == CircuitElement.OUTVAR) {
						Connection foundConnection = new Connection(currentElem.LocalID, outVar.getFormalParameter(),
								nextElem.LocalID, nextElem.outvar.getExpression());
						if (currentElementConnectionSet.size() == 0) {
							currentElementConnectionSet.add(foundConnection);
						} else if (currentElementConnectionSet.size() != 0
								&& !compareConnection(foundConnection, currentElementConnectionSet)) {
							currentElementConnectionSet.add(foundConnection);
						}
					}
				}
			}
		}
		return currentElementConnectionSet;
	}

	public Set<CircuitElement> getDIVandMUXInClockNet(ClockNet cn, CircuitInfo ci) {
		Set<CircuitElement> DIVandMUXSet = new HashSet<CircuitElement>();
		// System.out.println("connection size: "+cn.clockNetConnection.size());
		for (Connection conn : cn.clockNetConnection) {
			CircuitElement elem = CircuitInfo.getElementByID(conn.start, ci.circuitElem);
			if (elem.type == CircuitElement.BLOCK
					&& (elem.block.getTypeName().equals("DIV") || elem.block.getTypeName().equals("PLL"))) {
				CircuitElement newElem = new CircuitElement(elem.type, elem.LocalID);
				DIVandMUXSet.add(newElem);
			} else if (elem.type == CircuitElement.BLOCK
					&& (elem.block.getTypeName().equals("MUX2") || elem.block.getTypeName().equals("MUX3"))) {
				CircuitElement newElem = new CircuitElement(elem.type, elem.LocalID);
				DIVandMUXSet.add(newElem);
			}
		}
		return DIVandMUXSet;
	}

	public Set<CircuitElement> getPLLInClockNet(ClockNet cn, CircuitInfo ci) {
		Set<CircuitElement> PLLSet = new HashSet<CircuitElement>();

		for (Connection conn : cn.clockNetConnection) {
			CircuitElement elem = CircuitInfo.getElementByID(conn.start, ci.circuitElem);
			if (elem.type == CircuitElement.INVAR) {
				CircuitElement newElem = new CircuitElement(elem.type, elem.LocalID);
				PLLSet.add(newElem);
			}
		}

		return PLLSet;
	}

	public Set<ClockNet> clockNetSetForSelectedIP = new HashSet<ClockNet>();
	public ClockNet clockNetContainedSelectedIP;
	public double totalPower = 0;

	public double calcPower(double cap, double volt, double freq) {
		return cap * volt * volt * freq;
	}

	public Set<ClockNet> makeClockNetSet(CircuitElement keyElem, Set<ClockNet> clockNetsForSelectedIP, CircuitInfo ci) {
		// Scanner scanner = new Scanner(System.in);
		// Set<ClockNet> cns;
		// cns = findClockNet(keyElem);
		// System.out.println("CN SIZE: " + cns.size());

		for (ClockNet cn : ci.clockNets) {
			if (cn.keyElement.LocalID == keyElem.LocalID) {
				ClockNet selectedClockNet = new ClockNet();
				selectedClockNet.keyElement = new CircuitElement(cn.keyElement.type, cn.keyElement.LocalID);
				selectedClockNet.param = cn.param;
				selectedClockNet.cap = cn.cap;
				selectedClockNet.frequency = cn.frequency;
				clockNetsForSelectedIP.add(selectedClockNet);
				for (CircuitElement nextKeyElem : cn.clockNetDIVandMUX) {
					boolean isInculded = false;
					for (ClockNet notDuplicateSelectedClockNet : clockNetSetForSelectedIP) {
						if (notDuplicateSelectedClockNet.keyElement.LocalID == nextKeyElem.LocalID)
							isInculded = true;
					}
					if (!isInculded)
						return makeClockNetSet(nextKeyElem, clockNetsForSelectedIP, ci);
				}
			}
		}
		// for (ClockNet cn: ci.clockNets) {
		// if(cn.keyElement.LocalID == keyElem.LocalID) {
		// ClockNet selectedClockNet = new ClockNet();
		// selectedClockNet.keyElement = new CircuitElement(cn.keyElement.type,
		// cn.keyElement.LocalID);
		// selectedClockNet.param = cn.param;
		// selectedClockNet.cap = cn.cap;
		// selectedClockNet.frequency = cn.frequency;
		// selectedClockNet.voltage = cn.voltage;
		// clockNetSetForSelectedIP.add(selectedClockNet);
		// for (CircuitElement nextKeyElem : cn.clockNetDIVandMUX) {
		// boolean isInculded = false;
		// for (ClockNet notDuplicateSelectedClockNet :
		// clockNetSetForSelectedIP) {
		// if (notDuplicateSelectedClockNet.keyElement.LocalID ==
		// nextKeyElem.LocalID)
		// isInculded = true;
		// }
		// if (!isInculded)
		// makeClockNetList(nextKeyElem, ci);
		// }
		// break;
		// }
		// }

		//
		// for (ClockNet cn : cns){
		// System.out.println("block size: " + cn.clockNetDIVandMUX.size());
		// for(CircuitElement block : cn.clockNetDIVandMUX) {
		//
		// System.out.println("BLOCK");
		// String selectedInput="";
		// if (block.block.getTypeName().equals("MUX")) {
		// System.out.println("Which input?");
		// for(IInVariableInBlock inVar : block.block.getInVariables()) {
		// System.out.print(inVar.getFormalParameter()+" ");
		// }
		// System.out.println();
		// selectedInput = scanner.nextLine();
		// }
		// for(ClockNet clockNet : CircuitInfo.clockNets) {
		// if(clockNet == cn)
		// continue;
		// if (clockNet.keyElement.LocalID == block.LocalID) {
		// if (clockNet.keyElement.type == CircuitElement.BLOCK &&
		// clockNet.keyElement.block.getTypeName().equals("MUX") &&
		// selectedInput.equals(clockNet.param)) {
		// clockNetSetForSelectedIP.add(clockNet);
		// for(CircuitElement nextKeyElem : clockNet.clockNetDIVandMUX) {
		// makeClockNetList (nextKeyElem);
		// }
		// } else if (clockNet.keyElement.type == CircuitElement.BLOCK &&
		// clockNet.keyElement.block.getTypeName().equals("DIV")) {
		// clockNetSetForSelectedIP.add(clockNet);
		// for(CircuitElement nextKeyElem : clockNet.clockNetDIVandMUX) {
		// makeClockNetList (nextKeyElem);
		// }
		// }
		// }
		// }
		// }
		// }
		return clockNetsForSelectedIP;
	}

	public void selectUsedClockNetforEachUsage() {
		for (CircuitInfo ci : StatCircuitAnalyzer.circuitInfos) {
			for (CircuitUsageMapInfo cumi : ci.mapList.cumi) {
				for (String usedIP : cumi.mappingIp) {
					Set<ClockNet> usedCNs = new HashSet<ClockNet>();
					usedCNs = makeClockNetSet(CircuitInfo.getIPByName(usedIP, ci.circuitElem), usedCNs, ci);
					for (ClockNet candidateUsedCN : usedCNs) {
						boolean isContained = false;
						for (ClockNet existCN : cumi.usedClockNets) {
							if (candidateUsedCN.keyElement.LocalID == existCN.keyElement.LocalID
									&& candidateUsedCN.param.equals(existCN.param))
								isContained = true;
						}
						if (!isContained) {
							cumi.usedClockNets.add(candidateUsedCN);
						}
					}
				}
			}
		}
	}

	public Set<ClockNet> findClockNet(CircuitElement keyElem, CircuitInfo ci) {
		Set<ClockNet> clockNetSet = new HashSet<ClockNet>();
		for (ClockNet cn : ci.clockNets) {
			if (cn.keyElement.LocalID == keyElem.LocalID)
				clockNetSet.add(cn);
		}
		return clockNetSet;
	}

	public void removeIrrelatedClockNetAndCalcEachClockNet() {
		for (CircuitInfo ci : StatCircuitAnalyzer.circuitInfos) {
			for (CircuitFreqInfo cfi : ci.mapList.cfi) {
				for (ClockNet cn : ci.clockNets) {
					if (cn.keyElement.type == CircuitElement.BLOCK
							&& cn.keyElement.block.getTypeName().startsWith("MUX")
							&& cn.keyElement.LocalID == cfi.localID) {
						if (!cn.param.contains(Double.toString(cfi.value)))
							ci.clockNets.remove(cn);
					}
				}
			}
			calcClockNetFreq(ci);
		}
	}

	public void calcClockNetFreq(CircuitInfo ci) {
		for (ClockNet cn : ci.clockNets) {
			for (CircuitElement ce : cn.clockNetPLL) {
				for (CircuitPLLInfo pll : ci.mapList.plls) {
					if (ce.LocalID == pll.localID) {
						cn.frequency = pll.value;
						calcClockNetFreqRecursive(cn, ci);
					}
				}
			}
		}
	}

	public void calcClockNetFreqRecursive(ClockNet cn, CircuitInfo ci) {
		for (ClockNet nextCN : ci.clockNets) {
			if (nextCN.frequency != -1)
				continue;
			for (CircuitElement ceDIVandMUX : nextCN.clockNetDIVandMUX) {
				if (cn.keyElement.LocalID == ceDIVandMUX.LocalID) {
					if (ceDIVandMUX.block.getTypeName().equals("DIV")) {
						nextCN.frequency = cn.frequency * CircuitInfo.getFreqByID(ceDIVandMUX.LocalID, ci);
						calcClockNetFreqRecursive(nextCN, ci);
					} else if (ceDIVandMUX.block.getTypeName().startsWith("MUX")) {
						nextCN.frequency = cn.frequency;
						calcClockNetFreqRecursive(nextCN, ci);
					}
				}
			}
		}
	}

	public void getFrequencies() {
		for (ClockNet cn : clockNetSetForSelectedIP) {
			Scanner scanner = new Scanner(System.in);
			System.out.println(
					"What is frequency of clockNet started with " + cn.keyElement.LocalID + ", parameter: " + cn.param);
			cn.frequency = Double.parseDouble(scanner.nextLine());
		}
	}

	// public void calculateCap (double totalPower, double voltage) {
	// double power = 0;
	// for (ClockNet cn : clockNetSetForSelectedIP) {
	// power += (double) cn.clockNetConnection.size() * cn.frequency
	// *cn.frequency *voltage;
	// }
	// double cap = totalPower / power;
	// for (ClockNet cn : clockNetSetForSelectedIP) {
	// cn.capacity = cn.clockNetConnection.size() * cap;
	// }
	// }

	// public void calculateFrequencies (ClockNet clockNet, double freq) {
	// clockNet.frequency *= freq;
	// for(Element keyElementOfClockNet : clockNet.clockNetDIVandMUX) {
	// for(ClockNet clockNetContainedKeyElem :
	// findClockNet(keyElementOfClockNet)) {
	// if (clockNetSetForSelectedIP.contains(clockNetContainedKeyElem)) {
	// if (keyElementOfClockNet.block.getTypeName().equals("DIV")) {
	// StringTokenizer st = new
	// StringTokenizer(keyElementOfClockNet.block.getInstanceName(), "/");
	// double numerator = Double.parseDouble(st.nextToken());
	// double denominator = Double.parseDouble(st.nextToken());
	//// double frequency = numerator/denominator;
	//
	// calculateFrequencies(clockNetContainedKeyElem, denominator);
	// } else if (keyElementOfClockNet.block.getTypeName().equals("MUX")) {
	// calculateFrequencies(clockNetContainedKeyElem, freq);
	// }
	// }
	// }
	// }
	// }

}
