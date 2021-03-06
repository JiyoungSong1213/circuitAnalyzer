package statCircuitAnalyzer;

import java.awt.Color;
import java.awt.Dimension;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import plcopen.inf.model.IPOU;
import plcopen.inf.model.IVariable;
import plcopen.inf.type.IConnection;
import plcopen.inf.type.IPosition;
import plcopen.inf.type.IVariableList;
import plcopen.inf.type.group.elementary.IElementaryType;
import plcopen.inf.type.group.fbd.IBlock;
import plcopen.inf.type.group.fbd.IInVariable;
import plcopen.inf.type.group.fbd.IInVariableInBlock;
import plcopen.inf.type.group.fbd.IOutVariable;
import plcopen.inf.type.group.fbd.IOutVariableInBlock;
import plcopen.model.ProjectImpl;
import plcopen.type.body.FBD;
import plcopen.xml.PLCModel;
import circuitRelated.CircuitInfo;
import circuitRelated.Connection;
import circuitRelated.CircuitElement;

public class LoadXMLFile {
	static ProjectImpl PLCProject = null;
	static int indexOfTopModule;
	final String outputPath = "";
	BufferedWriter writer;
	long IDcount = 0;

	public void initialize() {

	}

	public void loadXML(String filePath) {
		initialize();

	}

	public void readFromXML(String file) {

	}

	public void makeClockNet() {

	}

	public void makeClockNetSet() {

	}

	public void getLibrary() {

	}

	public void setBasicInfo(String filePath, CircuitInfo ci) throws IOException {

		File file = new File(filePath);
		PLCProject = (ProjectImpl) PLCModel.readFromXML(file);

		FileWriter fw = new FileWriter(new File("log.txt"));

		IPOU POU = PLCProject.getPOUs().get(indexOfTopModule);
		FBD ld = (FBD) POU.getBody();

		ci.circuitName = POU.getName();

		System.out.println("Circuit Name: ");
		System.out.println(ci.circuitName);
		System.out.println("[IN-VARIABLEs and OSCILIATORs]");

		fw.write("[IN-VARIABLEs and OSCILIATORs]");
		fw.write(System.lineSeparator());

		fw.write("size: " + ld.getInVariables());
		fw.write(System.lineSeparator());
		for (IInVariable in : ld.getInVariables()) {
			in.setInitialLocalID(in.getLocalID());
			CircuitElement elem = new CircuitElement(CircuitElement.INVAR, in.getLocalID());
			elem.invar = in;

			ci.circuitElem.add(elem);
			ci.inputs.add(elem);

			System.out.println(in.getLocalID() + " " + in.getExpression());
		}

		List<IVariableList> interfaceInVar = POU.getInterface().getInputVars();
		for (IVariableList ivl : interfaceInVar) {
			for (IVariable iVar : ivl.getVariables()) {
				for (CircuitElement inputElem : ci.inputs) {
					if (iVar.getName().equals(inputElem.invar.getExpression())) {
						IElementaryType inputType = (IElementaryType) iVar.getType();
						if (inputType.getTypeName().equals("REAL"))
							inputElem.valueType = CircuitElement.OSCILATOR;
						else
							inputElem.valueType = CircuitElement.INPUT;
						// to do
						// iVar.getInitialValue()
						break;
					}
				}
			}
		}

		for (IInVariable in : ld.getInVariables()) {
			fw.write(in.getLocalID() + " " + in.getExpression());
			fw.write(System.lineSeparator());
		}

		System.out.println();
		fw.write(System.lineSeparator());

		System.out.println("[IPs and OUT-VARIABLEs]");
		fw.write("[IPs and OUT-VARIABLEs]");
		fw.write(System.lineSeparator());

		fw.write("size: " + ld.getOutVariables());
		fw.write(System.lineSeparator());
		for (IOutVariable out : ld.getOutVariables()) {
			out.setInitialLocalID(out.getLocalID());

			CircuitElement elem = new CircuitElement(CircuitElement.OUTVAR, out.getLocalID());
			elem.outvar = out;

			ci.circuitElem.add(elem);
			ci.IPs.add(elem);

			System.out.println(out.getLocalID() + " " + out.getExpression());
			fw.write(out.getLocalID() + " " + out.getExpression());
			fw.write(System.lineSeparator());
		}

		List<IVariableList> interfaceOutVar = POU.getInterface().getOutputVars();
		for (IVariableList ivl : interfaceOutVar) {
			for (IVariable iVar : ivl.getVariables()) {
				for (CircuitElement outputElem : ci.IPs) {
					if (iVar.getName().equals(outputElem.outvar.getExpression())) {
						IElementaryType outputType = (IElementaryType) iVar.getType();
						if (outputType.getTypeName().equals("INT"))
							outputElem.valueType = CircuitElement.IP;
						else
							outputElem.valueType = CircuitElement.OUTPUT;
						// to do
						// iVar.getInitialValue()
						break;
					}
				}
			}
		}

		for (IOutVariable out : ld.getOutVariables()) {
			fw.write(out.getLocalID() + " " + out.getExpression());
			fw.write(System.lineSeparator());
		}

		System.out.println();
		System.out.println("[BLOCKs]");

		fw.write(System.lineSeparator());
		fw.write("[BLOCKs]");
		fw.write(System.lineSeparator());

		fw.write("size: " + ld.getBlocks());
		fw.write(System.lineSeparator());

		for (IBlock block : ld.getBlocks()) {
			block.setInitialLocalID(block.getLocalID());

			CircuitElement elem = new CircuitElement(CircuitElement.BLOCK, block.getLocalID());
			elem.block = block;
			// to do list
			if (elem.block.getTypeName().equals("REAL_TO_DINT"))
				elem.block.setTypeName("DIV");
			else if (elem.block.getTypeName().equals("MAX2_DINT"))
				elem.block.setTypeName("MUX");
			else if (elem.block.getTypeName().equals("MAX3_DINT"))
				elem.block.setTypeName("MUX2");
			else if (elem.block.getTypeName().equals("MAX4_DINT"))
				elem.block.setTypeName("MUX3");
			else if (elem.block.getTypeName().equals("MAX4_REAL"))
				elem.block.setTypeName("MUX3");
			else if (elem.block.getTypeName().equals("MIN2_DINT"))
				elem.block.setTypeName("PLL");
			else if (elem.block.getTypeName().equals("MIN2_REAL"))
				elem.block.setTypeName("PLL");
			else if (elem.block.getTypeName().equals("TIME_TO_REAL"))
				elem.block.setTypeName("GATE");
			else if (elem.block.getTypeName().equals("DINT_TO_REAL"))
				elem.block.setTypeName("BUFFER");

			ci.circuitElem.add(elem);
			ci.blocks.add(elem);
			System.out.println(block.getLocalID() + " " + block.getTypeName());
		}

		System.out.println();

		for (IBlock block : ld.getBlocks()) {
			fw.write(block.getLocalID() + " " + block.getTypeName());
			fw.write(System.lineSeparator());
		}

		System.out.println();
		System.out.println("[CONNECTIONS]");

		fw.write(System.lineSeparator());
		fw.write("[CONNECTIONS]");
		fw.write(System.lineSeparator());
		for (CircuitElement block : ci.blocks) {
			for (IInVariableInBlock inVar : block.block.getInVariables()) {
				for (IConnection conn : inVar.getConnectionPointIn().getConnections()) {
					CircuitElement prevelem = CircuitInfo.getElementByID(conn.getRefLocalID(), ci.circuitElem);
					if (prevelem == null)
						continue;
					prevelem.nextElement.add(block);
					block.prevElement.add(prevelem);

					Connection newCon = new Connection(prevelem.LocalID, (prevelem.type == CircuitElement.BLOCK)
							? (conn.getFormalParam()) : (prevelem.invar.getExpression()), block.LocalID,
							inVar.getFormalParameter());
					newCon.conn = conn;
					if (inVar.isNegated()) {
						newCon.negated = true;
						System.out.print(" ~ ");
					}

					ci.connections.add(newCon);
					prevelem.FormalParam = inVar.getFormalParameter();

					if (prevelem.type == CircuitElement.BLOCK) {
						System.out.println(prevelem.LocalID + prevelem.block.getTypeName() + " <-> " + block.LocalID
								+ " " + block.block.getTypeName());
						fw.write(prevelem.LocalID + prevelem.block.getTypeName() + " <-> " + block.LocalID + " "
								+ block.block.getTypeName());
						fw.write(System.lineSeparator());
					} else if (prevelem.type == CircuitElement.INVAR) {
						System.out.println(prevelem.LocalID + " " + prevelem.invar.getExpression() + " <-> "
								+ block.LocalID + " " + block.block.getTypeName());
						fw.write(prevelem.LocalID + " " + prevelem.invar.getExpression() + " <-> " + block.LocalID + " "
								+ block.block.getTypeName());
						fw.write(System.lineSeparator());
					}
				}
			}
		}

		for (CircuitElement outVar : ci.IPs) {
			for (IConnection conn : outVar.outvar.getConnectionPointIn().getConnections()) {
				CircuitElement prevElem = CircuitInfo.getElementByID(conn.getRefLocalID(), ci.circuitElem);
				if (prevElem == null)
					continue;
				if (prevElem.block != null) {
					Connection newCon = new Connection(prevElem.LocalID, conn.getFormalParam(), outVar.LocalID,
							outVar.outvar.getExpression());
					if (outVar.outvar.isNegated()) {
						newCon.negated = true;
						System.out.println(" ~ ");
					}
					ci.connections.add(newCon);
					System.out.println(conn.getFormalParam() + " / " + outVar.outvar.getExpression());
					fw.write("Block" + prevElem.block.getTypeName() + ": " + conn.getFormalParam() + " / "
							+ outVar.outvar.getExpression());
					fw.write(System.lineSeparator());

					System.out.println(prevElem.LocalID + " " + prevElem.block.getTypeName() + " <-> " + outVar.LocalID
							+ " " + outVar.outvar.getExpression());
					fw.write(prevElem.LocalID + " " + prevElem.block.getTypeName() + " <-> " + outVar.LocalID + " "
							+ outVar.outvar.getExpression());
					fw.write(System.lineSeparator());
					prevElem.nextElement.add(outVar);
					outVar.prevElement.add(prevElem);
				}
			}
		}

		fw.close();
	}

	public DrawPanel drawPicture(DrawPanel dp, CircuitInfo ci, boolean isInit) {
		// //first open
		if (ci == null) {
			dp.g2d.setColor(Color.white);
			dp.g2d.fillRect(0, 0, 50000, 50000);
			return dp;
		}

		// draw IPs
		for (CircuitElement IP : ci.IPs) {
			String IPname = "(" + Long.toString(IP.outvar.getLocalID()) + ")" + IP.outvar.getExpression();
			drawChar(dp, IPname.toCharArray(), 0, IPname.length(), IP.outvar.getPosition().getX(),
					IP.outvar.getPosition().getY() - 3);
		}

		// draw blocks
		for (CircuitElement block : ci.blocks) {
			String blockName = "(" + Long.toString(block.block.getLocalID()) + ")" + block.block.getTypeName();
			dp.g2d.setColor(Color.red);
			drawRect(dp, block.block.getPosition().getX(), block.block.getPosition().getY() + 7,
					block.block.getSize().getWidth(), block.block.getSize().getHeight() + 18);
			dp.g2d.setColor(Color.black);
			drawChar(dp, blockName.toCharArray(), 0, blockName.length(), block.block.getPosition().getX() + 2,
					block.block.getPosition().getY() + 18);
			int inVarCnt = 0;
			for (IInVariableInBlock in : block.block.getInVariables()) {
				String formalParam = in.getFormalParameter();
				drawChar(dp, formalParam.toCharArray(), 0, formalParam.length(), block.block.getPosition().getX() + 3,
						block.block.getPosition().getY() + 30 + inVarCnt * 30 + 6);
				inVarCnt++;
			}
			int outVarCnt = 0;
			for (IOutVariableInBlock out : block.block.getOutVariables()) {
				String formalParam = "OUT";
				drawChar(dp, formalParam.toCharArray(), 0, formalParam.length(),
						block.block.getPosition().getX() + block.block.getSize().getWidth() + 3,
						block.block.getPosition().getY() + 30 + outVarCnt * 30 + 11);
				outVarCnt++;
			}
		}

		// draw connections
		for (CircuitElement block : ci.blocks) {
			CircuitElement nextBlock = CircuitInfo.getElementByID(block.block.getLocalID(), ci.circuitElem);
			for (IInVariableInBlock in : block.block.getInVariables()) {
				for (IConnection conn : in.getConnectionPointIn().getConnections()) {
					drawConnection(dp, nextBlock, conn, Color.black, 0, 0, ci);
					// todo: change false to have conditions
				}
			}
		}

		for (CircuitElement outVar : ci.IPs) {
			CircuitElement nextElem = CircuitInfo.getElementByID(outVar.outvar.getLocalID(), ci.circuitElem);
			for (IConnection conn : outVar.outvar.getConnectionPointIn().getConnections()) {
				drawConnection(dp, nextElem, conn, Color.black, 0, 0, ci);
				// todo: change false to have conditions
			}
		}

		// draw inputs
		for (CircuitElement inVar : ci.inputs) {
			String inputName = "(" + Long.toString(inVar.invar.getLocalID()) + ")" + inVar.invar.getExpression();
			dp.g2d.setColor(Color.black);
			drawChar(dp, inputName.toCharArray(), 0, inputName.length(), inVar.invar.getPosition().getX(),
					inVar.invar.getPosition().getY() - 3);
		}

		return dp;

	}

	public void drawChar(DrawPanel dp, char[] data, int offset, int length, int x, int y) {
		dp.g2d.drawChars(data, offset, length, x, y);
		extendPanelSize(dp, x + 10 * length, y + 20);
	}

	public void extendPanelSize(DrawPanel dp, int x, int y) {
		Dimension current = dp.getPreferredSize();
		if (current.width < x)
			current.width = x;
		if (current.height > y)
			current.width = y;
		dp.setPreferredSize(current);
	}

	public void drawRect(DrawPanel dp, int x, int y, int width, int height) {
		dp.g2d.drawRect(x, y, width, height);
		extendPanelSize(dp, x + width, y + height);
	}

	public void drawLine(DrawPanel dp, int x1, int y1, int x2, int y2) {
		dp.g2d.drawLine(x1, y1, x2, y2);
		extendPanelSize(dp, x1, y1);
		extendPanelSize(dp, x2, y2);
	}

	public void drawConnection(DrawPanel dp, CircuitElement nextElem, IConnection conn, Color color, int x, int y,
			CircuitInfo ci) {

		dp.g2d.setColor(color);
		conn.getRefLocalID();
		CircuitElement prevElem = CircuitInfo.getElementByID(conn.getRefLocalID(), ci.circuitElem);
		IPosition before = null;

		for (IPosition current : conn.getPositions()) {
			if (before != null) {
				if (before.getX() == current.getX() || before.getY() == current.getY()) {
					drawLine(dp, before.getX() + x, before.getY() + y, current.getX() + x, current.getY() + y);
				} else {
					dp.g2d.drawRect(before.getX() - 1 + x, current.getY() - 1 + y, 2, 2);
					drawLine(dp, before.getX() + x, before.getY() + y, before.getX() + x, current.getY() + y);
				}
			} else {
				dp.g2d.drawRect(current.getX() - 1 + x, current.getY() - 1 + y, 2, 2);
			}
			before = current;
		}

		if (before != null) {
			drawLine(dp, before.getX() + x, before.getY() + y, before.getX() - 80 + x, before.getY() + y);
			dp.g2d.drawRect(before.getX() - 81 + x, before.getY() - 1 + y, 2, 2);
		} else {
			CreateGUI.consolePrintln("Error: No graphical information detected in Connection between "
					+ prevElem.LocalID + " and " + nextElem.LocalID);
			if (prevElem.type == CircuitElement.INVAR) {
				drawLine(dp, prevElem.invar.getPosition().getX(), prevElem.invar.getPosition().getY(),
						nextElem.block.getPosition().getX(),
						nextElem.block.getPosition().getY() + 1 * nextElem.block.getSize().getHeight() / 2);
			} else if (nextElem.type == CircuitElement.OUTVAR) {
				drawLine(dp, prevElem.block.getPosition().getX() + prevElem.block.getSize().getWidth(),
						prevElem.block.getPosition().getY() + prevElem.block.getSize().getHeight() / 2,
						nextElem.outvar.getPosition().getX(), nextElem.outvar.getPosition().getY());
			} else {
				drawLine(dp, prevElem.block.getPosition().getX() + prevElem.block.getSize().getWidth(),
						prevElem.block.getPosition().getY() + prevElem.block.getSize().getHeight() / 2,
						nextElem.block.getPosition().getX(),
						nextElem.block.getPosition().getY() + 1 * nextElem.block.getSize().getHeight() / 2);
			}
			CreateGUI.consoleFlush();
		}
	}
}
