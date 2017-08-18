package statCircuitAnalyzer;

import java.util.HashSet;
import java.util.Set;

import plcopen.inf.type.IConnection;
import plcopen.inf.type.group.fbd.IInVariableInBlock;
import plcopen.inf.type.group.fbd.IOutVariableInBlock;
import circuitRelated.CircuitClockInfo;
import circuitRelated.CircuitElement;
import circuitRelated.CircuitFreqInfo;
import circuitRelated.CircuitInfo;
import circuitRelated.CircuitUsageMapInfo;
import circuitRelated.ClockNet;
import circuitRelated.Connection;

public class CircuitAnalyzer {

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
		for (CircuitElement IP : ci.IPs) {
			ClockNet newClockNet = new ClockNet();
			newClockNet.keyElement = CircuitInfo.getElementByID(IP.LocalID, ci.circuitElem); // keyElement;
			newClockNet.clockNetConnection = makeClockNet(IP, ci);
			newClockNet.clockNetDIVandMUX = getDIVandMUXInClockNet(newClockNet, ci);
			newClockNet.clockNetPLL = getPLLInClockNet(newClockNet, ci);
			ci.clockNets.add(newClockNet);
		}
		for (CircuitElement block : ci.blocks) {
			if (block.block.getTypeName().equals("DIV") || block.block.getTypeName().equals("PLL")) {
				ClockNet newClockNet = new ClockNet();
				newClockNet.keyElement = CircuitInfo.getElementByID(block.LocalID, ci.circuitElem);// keyElement;
				newClockNet.clockNetConnection = makeClockNet(block, ci);
				newClockNet.clockNetDIVandMUX = getDIVandMUXInClockNet(newClockNet, ci);
				newClockNet.clockNetPLL = getPLLInClockNet(newClockNet, ci);
				ci.clockNets.add(newClockNet);
			}
			if (block.block.getTypeName().startsWith("MUX")) {
				for (IInVariableInBlock inVar : block.block.getInVariables()) {
					ClockNet newClockNet = new ClockNet();
					newClockNet.keyElement = CircuitInfo.getElementByID(block.LocalID, ci.circuitElem);// keyElement;
					newClockNet.param = inVar.getFormalParameter();
					newClockNet.clockNetConnection = makeClockNet(block, newClockNet.param, ci);
					newClockNet.clockNetDIVandMUX = getDIVandMUXInClockNet(newClockNet, ci);
					newClockNet.clockNetPLL = getPLLInClockNet(newClockNet, ci);
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
		if (curElem.prevElement.size() != 0) {
			for (CircuitElement prevElem : curElem.prevElement) {
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
				if (prevElem.type == CircuitElement.BLOCK && prevElem.block.getTypeName().startsWith("MUX"))
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
		}

		if (curElem.type == CircuitElement.BLOCK) {
			for (IInVariableInBlock inVar : curElem.block.getInVariables()) {
				for (IConnection conn : inVar.getConnectionPointIn().getConnections()) {
					CircuitElement prevElement = CircuitInfo.getElementByID(conn.getRefLocalID(), ci.circuitElem);
					Connection foundConnection = new Connection(prevElement.LocalID,
							(prevElement.type == CircuitElement.BLOCK) ? (conn.getFormalParam())
									: (prevElement.invar.getExpression()),
							curElem.LocalID, inVar.getFormalParameter());
					foundConnection.conn = conn;
					if (currentElementConnectionSet.size() == 0) {
						currentElementConnectionSet.add(foundConnection);
					} else if (currentElementConnectionSet.size() != 0
							&& !compareConnection(foundConnection, currentElementConnectionSet)) {
						currentElementConnectionSet.add(foundConnection);
					}
				}
			}
		} else if (curElem.type == CircuitElement.OUTVAR) {
			for (IConnection conn : curElem.outvar.getConnectionPointIn().getConnections()) {
				CircuitElement prevElement = CircuitInfo.getElementByID(conn.getRefLocalID(), ci.circuitElem);
				Connection foundConnection = new Connection(prevElement.LocalID,
						(prevElement.type == CircuitElement.BLOCK) ? (conn.getFormalParam())
								: (prevElement.invar.getExpression()),
						curElem.LocalID, curElem.outvar.getExpression());
				foundConnection.conn = conn;
				if (currentElementConnectionSet.size() == 0) {
					currentElementConnectionSet.add(foundConnection);
				} else if (currentElementConnectionSet.size() != 0
						&& !compareConnection(foundConnection, currentElementConnectionSet)) {
					currentElementConnectionSet.add(foundConnection);
				}
			}
		}
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
					if (prevElement.type == CircuitElement.BLOCK && (prevElement.block.getTypeName().startsWith("MUX")))
						continue;
					currentElementConnectionSet.addAll(makeClockNet(prevElement, ci));
				}
				for (IConnection conn : inVar.getConnectionPointIn().getConnections()) {
					CircuitElement prevElement = CircuitInfo.getElementByID(conn.getRefLocalID(), ci.circuitElem);
					Connection foundConnection = new Connection(prevElement.LocalID,
							(prevElement.type == CircuitElement.BLOCK) ? (conn.getFormalParam())
									: (prevElement.invar.getExpression()),
							curElem.LocalID, inVar.getFormalParameter());
					foundConnection.conn = conn;
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
										&& (nextElem.block.getTypeName().startsWith("MUX")))
									continue;
								currentElementConnectionSet.addAll(makeClockNetForward(nextElem, ci));
							}
						}
						for (IInVariableInBlock inVar : nextElem.block.getInVariables()) {
							for (IConnection conn : inVar.getConnectionPointIn().getConnections()) {
								Connection foundConnection = new Connection(currentElem.LocalID,
										outVar.getFormalParameter(), nextElem.LocalID, inVar.getFormalParameter());
								foundConnection.conn = conn;
								if (currentElementConnectionSet.size() == 0) {
									currentElementConnectionSet.add(foundConnection);
								} else if (currentElementConnectionSet.size() != 0
										&& !compareConnection(foundConnection, currentElementConnectionSet)) {
									currentElementConnectionSet.add(foundConnection);
								}
							}
						}
					} else if (nextElem.type == CircuitElement.OUTVAR) {
						for (IConnection conn : nextElem.outvar.getConnectionPointIn().getConnections()) {
							Connection foundConnection = new Connection(currentElem.LocalID,
									outVar.getFormalParameter(), nextElem.LocalID, nextElem.outvar.getExpression());
							foundConnection.conn = conn;
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
		}
		return currentElementConnectionSet;
	}

	public Set<CircuitElement> getDIVandMUXInClockNet(ClockNet cn, CircuitInfo ci) {
		Set<CircuitElement> DIVandMUXSet = new HashSet<CircuitElement>();
		for (Connection conn : cn.clockNetConnection) {
			CircuitElement elem = CircuitInfo.getElementByID(conn.start, ci.circuitElem);
			if (elem.type == CircuitElement.BLOCK
					&& (elem.block.getTypeName().equals("DIV") || elem.block.getTypeName().equals("PLL"))) {
				CircuitElement newElem = new CircuitElement(elem.type, elem.LocalID);
				DIVandMUXSet.add(newElem);
			} else if (elem.type == CircuitElement.BLOCK && (elem.block.getTypeName().startsWith("MUX"))) {
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
		for (ClockNet cn : ci.clockNets) {
			if (cn.keyElement.LocalID == keyElem.LocalID) {
				if (cn.isUsed)
					clockNetsForSelectedIP.add(cn);
				else
					return clockNetsForSelectedIP;
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
		return clockNetsForSelectedIP;
	}

	public void selectUsedClockNetforEachUsage() {
		for (CircuitInfo ci : StatCircuitAnalyzer.circuitInfos) {
			if (ci == null)
				break;
			for (CircuitUsageMapInfo cumi : ci.mapList.CUMIs) {
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
			if (ci == null)
				break;
			for (CircuitFreqInfo cfi : ci.mapList.CFIs) {
				for (ClockNet cn : ci.clockNets) {
					if (cn.keyElement.type == CircuitElement.BLOCK
							&& cn.keyElement.block.getTypeName().startsWith("MUX")
							&& cn.keyElement.LocalID == cfi.localID) {
						if (!cn.param.contains(Integer.toString((int) cfi.value))) {
							cn.isUsed = false;
						}
					}
				}
			}
			calcClockNetFreq(ci);
		}
	}

	public void calcClockNetFreq(CircuitInfo ci) {
		for (ClockNet cn : ci.clockNets) {
			for (CircuitElement ce : cn.clockNetPLL) {
				for (CircuitClockInfo pll : ci.mapList.CLOCKs) {
					if (ce.LocalID == pll.localID) {
						cn.frequency = pll.value;
					}
				}
			}
		}
		
		for (ClockNet cn: ci.clockNets) {
			if(cn.frequency >= 0)
				calcClockNetFreqRecursive(cn, ci);
		}
	}

	public void calcClockNetFreqRecursive(ClockNet cn, CircuitInfo ci) {
		for (ClockNet nextCN : ci.clockNets) {
			if (nextCN.frequency > -1)
				continue;
			for (CircuitElement ceDIVandMUX : nextCN.clockNetDIVandMUX) {
				if (cn.keyElement.LocalID == ceDIVandMUX.LocalID) {
					if (cn.keyElement.block.getTypeName().equals("DIV")) {
						nextCN.frequency = cn.frequency * CircuitInfo.getFreqByID(ceDIVandMUX.LocalID, ci);
						calcClockNetFreqRecursive(nextCN, ci);
					} else if (cn.keyElement.block.getTypeName().startsWith("MUX") && cn.isUsed) {
						nextCN.frequency = cn.frequency;
						calcClockNetFreqRecursive(nextCN, ci);
					} else if (cn.keyElement.block.getTypeName().startsWith("PLL")) {
						nextCN.frequency = cn.frequency * CircuitInfo.getFreqByID(ceDIVandMUX.LocalID, ci);
						calcClockNetFreqRecursive(nextCN, ci);
					}
				}
			}
		}
	}
}
