package statCircuitAnalyzer;

import java.awt.Color;
import java.awt.Dimension;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

import plcopen.inf.model.IPOU;
import plcopen.inf.model.IVariable;
import plcopen.inf.type.IConnection;
import plcopen.inf.type.IDataType;
import plcopen.inf.type.IVariableList;
import plcopen.inf.type.group.elementary.IElementaryType;
import plcopen.inf.type.group.fbd.IBlock;
import plcopen.inf.type.group.fbd.IInVariable;
import plcopen.inf.type.group.fbd.IInVariableInBlock;
import plcopen.inf.type.group.fbd.IOutVariable;
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
	
	public void initialize () {
		
	}
	
	public void loadXML (String filePath) {
		initialize();
		
		
	}
	
	public void readFromXML (String file) {
		
	}
	
	public void makeClockNet () {
	
	}
	
	public void makeClockNetSet () {
	
	}
	
	public void getLibrary() {
		
	}
	
	
//	public void parseXML (String inputFile) {
//		File circuitXMLfile = new File(inputFile);
//		DocumentBuilderFactory docBuildFactory = DocumentBuilderFactory.newInstance();
//		DocumentBuilder docBuilder = docBuildFactory.newDocumentBuilder();
//		Document docForCircuit = docBuilder.parse(circuitXMLfile);
//		docForCircuit.getDocumentElement().normalize();
//		
//		NodeList inputVarsList = docForCircuit.getElementsByTagName("inputVars");
//		for (int i = 0; i <inputVarsList.getLength(); i++) {
//			Node inputVarsNode = inputVarsList.item(i);
//			NodeList inputVarNodes = inputVarsNode.getChildNodes();
//			for (int j = 0; j < inputVarNodes.getLength(); j++) {
//				Node inputVar = inputVarNodes.item(j);
//				Element inputVarElem = (Element) inputVar;
//				
//				String inputName = inputVarElem.getAttribute("name");
//				String inputType = inputVarElem.getElementsByTagName("type").item(0).getTextContent();
//				Node inputValNode = inputVarElem.getElementsByTagName("initialValue").item(0);
//				Element inputValElem = (Element) inputValNode;
//				double inputVal = Double.parseDouble(inputValElem.getElementsByTagName("value").item(0).getTextContent());
//			
//				InVariable inVar = new InVariable();
//				inVar.setExpression(inputName);
//				if(inputType.equals("INPUT"))
//					inVar.setType(InVariable.INPUT);
//				else if (inputType.equals("OSCILATOR"))
//					inVar.setType(InVariable.OSCILATOR);
//				inVar.setValue(inputVal);
////				inVar.setInitialLocalID(++IDcount);
//				
//				NodeList bodyinVarList = docForCircuit.getElementsByTagName("inVariables");
//				for (int k = 0; k < bodyinVarList.getLength(); k++) {
//					Node bodyinVarNode = bodyinVarList.item(k);
//					Element bodyinVarElem = (Element) bodyinVarNode;
//					if (bodyinVarElem.getElementsByTagName("expression").equals(inputName)) {
//						inVar.setValue(Long.parseLong(bodyinVarElem.getAttribute("localId")));
//						inVar.setIDwidth(Integer.parseInt(bodyinVarElem.getAttribute("width")));
//						inVar.setIDheight(Integer.parseInt(bodyinVarElem.getAttribute("height")));
//						Element inVarPos = (Element) bodyinVarElem.getElementsByTagName("position").item(0);
//						inVar.set
//					}
//					
//				}
//				
//				CircuitInfo.inVariables.add(inVar);
//			}
//		}
//		
//		NodeList outputVarsList = docForCircuit.getElementsByTagName("outputVars");
//		for (int i = 0; i <outputVarsList.getLength(); i++) {
//			Node outputVarsNode = inputVarsList.item(i);
//			NodeList outputVarNodes = outputVarsNode.getChildNodes();
//			for (int j = 0; j < outputVarNodes.getLength(); j++) {
//				Node outputVar = outputVarNodes.item(j);
//				Element outputVarElem = (Element) outputVar;
//				
//				String outputName = outputVarElem.getAttribute("name");
//				String outputType = outputVarElem.getElementsByTagName("type").item(0).getTextContent();
//				Node outputValNode = outputVarElem.getElementsByTagName("initialValue").item(0);
//				Element outputValElem = (Element) outputValNode;
//				double outputVal = Double.parseDouble(outputValElem.getElementsByTagName("value").item(0).getTextContent());
//			
//				OutVariable outVar = new OutVariable();
//				outVar.setExpression(outputName);
//				if(outputType.equals("INPUT"))
//					outVar.setType(InVariable.INPUT);
//				else if (outputType.equals("OSCILATOR"))
//					outVar.setType(InVariable.OSCILATOR);
//				outVar.setValue(outputVal);
//				outVar.setInitialLocalID(++IDcount);
//				
//				CircuitInfo.outVariables.add(outVar);
//			}
//		}
//		NodeList blockLisit = docForCircuit.getElementsByTagName("block");
//	}
	
	public void setBasicInfo(String filePath) throws IOException {
		
		File file = new File(filePath);
		PLCProject = (ProjectImpl) PLCModel.readFromXML(file);
		
		FileWriter fw = new FileWriter(new File("log.txt"));
		
		IPOU POU = PLCProject.getPOUs().get(indexOfTopModule);
		FBD ld = (FBD) POU.getBody();
		
		System.out.println("Circuit Name: ");
		System.out.println("[IN-VARIABLEs and OSCILIATORs]");
		
		fw.write("[IN-VARIABLEs and OSCILIATORs]");
		fw.write(System.lineSeparator());
		
		fw.write("size: "+ ld.getInVariables());
		fw.write(System.lineSeparator());
		for (IInVariable in : ld.getInVariables()) {
			in.setInitialLocalID(in.getLocalID());
			CircuitElement elem = new CircuitElement(CircuitElement.INVAR, in.getLocalID());
			elem.invar = in;
			
			CircuitInfo.circuitElem.add(elem);
			CircuitInfo.inputs.add(elem);
			
			System.out.println(in.getLocalID() + " " + in.getExpression());
		}
		
		List<IVariableList> interfaceInVar = POU.getInterface().getInputVars();
		for(IVariableList ivl : interfaceInVar) {
			for (IVariable iVar : ivl.getVariables()) {
				for (CircuitElement inputElem : CircuitInfo.inputs) {
					if (iVar.getName().equals(inputElem.invar.getExpression())) {
						IElementaryType inputType = (IElementaryType) iVar.getType();
						if (inputType.getTypeName().equals("REAL"))
							inputElem.valueType = CircuitElement.OSCILATOR;
						else
							inputElem.valueType = CircuitElement.INPUT;
						//to do 
//						iVar.getInitialValue()
						break;
					}
				}
			}
		}
		
//		System.out.println("in var size: "+ interfaceInVar.size());
		
		for (IInVariable in : ld.getInVariables()) {
			fw.write(in.getLocalID() + " " + in.getExpression());
			fw.write(System.lineSeparator());
		}
		
		System.out.println();
		fw.write(System.lineSeparator());
		
		System.out.println("[IPs and OUT-VARIABLEs]");
		fw.write("[IPs and OUT-VARIABLEs]");
		fw.write(System.lineSeparator());
		
		fw.write("size: "+ ld.getOutVariables());
		fw.write(System.lineSeparator());
		for (IOutVariable out : ld.getOutVariables()) {
			out.setInitialLocalID(out.getLocalID());
			
			CircuitElement elem = new CircuitElement(CircuitElement.OUTVAR, out.getLocalID());
			elem.outvar = out;
			
			CircuitInfo.circuitElem.add(elem);
			CircuitInfo.IPs.add(elem);
			
			System.out.println(out.getLocalID() + " " + out.getExpression());
			fw.write(out.getLocalID() + " " + out.getExpression());
			fw.write(System.lineSeparator());
		}
		
		List<IVariableList> interfaceOutVar = POU.getInterface().getOutputVars();
		for(IVariableList ivl : interfaceOutVar) {
			for (IVariable iVar : ivl.getVariables()) {
				for (CircuitElement outputElem : CircuitInfo.IPs) {
					if (iVar.getName().equals(outputElem.outvar.getExpression())) {
						IElementaryType outputType = (IElementaryType) iVar.getType();
						if (outputType.getTypeName().equals("INT"))
							outputElem.valueType = CircuitElement.IP;
						else
							outputElem.valueType = CircuitElement.OUTPUT;
						//to do 
//						iVar.getInitialValue()
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
		
		fw.write("size: "+ ld.getBlocks());
		fw.write(System.lineSeparator());
		
		for (IBlock block : ld.getBlocks()) {
			block.setInitialLocalID(block.getLocalID());
			
			CircuitElement elem = new CircuitElement(CircuitElement.BLOCK, block.getLocalID());
			elem.block = block;
			//to do list
			if (elem.block.getTypeName().equals("REAL_TO_DINT"))
				elem.block.setTypeName("DIV");
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
			
			CircuitInfo.circuitElem.add(elem);
			CircuitInfo.blocks.add(elem);
			System.out.println(block.getLocalID() + " "+ block.getTypeName());
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
		for (CircuitElement block : CircuitInfo.blocks) {
			for (IInVariableInBlock inVar : block.block.getInVariables()) {
				for (IConnection conn : inVar.getConnectionPointIn().getConnections()) {
					CircuitElement prevelem = CircuitInfo.getElementByID(conn.getRefLocalID());
					if (prevelem == null)
						continue;
					prevelem.nextElement.add(block);
					block.prevElement.add(prevelem);
					
					Connection newCon = new Connection(prevelem.LocalID, (prevelem.type == CircuitElement.BLOCK) ? (conn.getFormalParam())
							: (prevelem.invar.getExpression()), block.LocalID, inVar.getFormalParameter());
					if (inVar.isNegated()) {
						newCon.negated = true;
						System.out.print(" ~ ");
					}
					
					CircuitInfo.connections.add(newCon);
					prevelem.FormalParam = inVar.getFormalParameter();
					
					if (prevelem.type == CircuitElement.BLOCK) {
						System.out.println(prevelem.LocalID + prevelem.block.getTypeName() + " <-> " + block.LocalID + " "
								+ block.block.getTypeName());
						fw.write(prevelem.LocalID + prevelem.block.getTypeName() + " <-> " + block.LocalID + " "
								+ block.block.getTypeName());
						fw.write(System.lineSeparator());
					} else if (prevelem.type == CircuitElement.INVAR) {
						System.out.println(prevelem.LocalID + " " + prevelem.invar.getExpression() + " <-> " + block.LocalID + " "
								+ block.block.getTypeName());
						fw.write(prevelem.LocalID + " " + prevelem.invar.getExpression() + " <-> " + block.LocalID + " "
								+ block.block.getTypeName());
						fw.write(System.lineSeparator());
					}
				}
			}
		}
				
		
		for (CircuitElement outVar : CircuitInfo.IPs) {
			for (IConnection conn : outVar.outvar.getConnectionPointIn().getConnections()) {
				CircuitElement prevElem = CircuitInfo.getElementByID(conn.getRefLocalID());
				if (prevElem == null)
					continue;
				if(prevElem.block != null) {
					Connection newCon = new Connection(prevElem.LocalID, conn.getFormalParam(), outVar.LocalID, outVar.outvar.getExpression());
					if (outVar.outvar.isNegated()) {
						newCon.negated = true;
						System.out.println(" ~ ");
					}
					CircuitInfo.connections.add(newCon);
					System.out.println(conn.getFormalParam() + " / " + outVar.outvar.getExpression());
					fw.write("Block" + prevElem.block.getTypeName() + ": " + conn.getFormalParam() + " / " + outVar.outvar.getExpression());
					fw.write(System.lineSeparator());
					
					System.out.println(prevElem.LocalID + " " + prevElem.block.getTypeName() + " <-> " + outVar.LocalID + " " + outVar.outvar.getExpression());
					fw.write(prevElem.LocalID + " " + prevElem.block.getTypeName() + " <-> " + outVar.LocalID + " " + outVar.outvar.getExpression());
					fw.write(System.lineSeparator());
					prevElem.nextElement.add(outVar);
					outVar.prevElement.add(prevElem);
				}
			}
		}
		
		fw.close();	
	}
	
	public void getBasicInfo() {
		
//		IPOU POU = PLCProject.getPOUs().get(indexOfTopModule);
//		FBD ld = (FBD) POU.getBody();
//		
//		CreateGUI.consolePrintln("Circuit Name: "+ PLCProject.getProjectName());
//		CreateGUI.consoleFlush();
//		
//		//In Variable info
//		CreateGUI.consolePrintln("[IN-VARIABLES]");
//		for (IInVariable in : ld.getInVariables()) {
//			in.setInitialLocalID(in.getLocalID());
//			
//			Element elem = new Element(Element.INVAR, in.getLocalID());
//			elem.invar = in;
//			
//			CircuitInfo.circuitElem.add(elem);
//			CircuitInfo.inputs.add(elem);
//			
//			CreateGUI.consolePrintln(in.getLocalID() + " " + in.getExpression());
//			CreateGUI.consoleFlush();
//		}
//		
//		//IP and Out Variable info
//		CreateGUI.consolePrintln("[IPs and OUT-VARIABLEs]");
//		for (IOutVariable out : ld.getOutVariables()) {
//			out.setInitialLocalID(out.getLocalID());
//			
//			Element elem = new Element(Element.OUTVAR, out.getLocalID());
//			elem.outvar = out;
//			
//			CircuitInfo.circuitElem.add(elem);
//			CircuitInfo.IPs.add(elem);
//			
//			CreateGUI.consolePrintln(out.getLocalID() + " " + out.getExpression());
//			CreateGUI.consoleFlush();
//		}
//		
//		//Block info
//		CreateGUI.consolePrintln("[BLOCKS]");
//		for (IBlock block : ld.getBlocks()) {
//			block.setInitialLocalID(block.getLocalID());
//			
//			Element elem = new Element(Element.BLOCK, block.getLocalID());
//			elem.block = block;
//			
//			CircuitInfo.circuitElem.add(elem);
//			CircuitInfo.blocks.add(elem);
//			
//			CreateGUI.consolePrintln(block.getLocalID() + " "+ block.getInstanceName());
//			CreateGUI.consoleFlush();
//		}
//		
//		//Connection info
//		CreateGUI.consolePrintln("[CONNECTIONS]");
//		for (Element block : CircuitInfo.blocks) {
//			for (IInVariableInBlock inVar : block.block.getInVariables()) {
//				for (IConnection conn : inVar.getConnectionPointIn().getConnections()) {
//					Element prevelem = CircuitInfo.getElementByID(conn.getRefLocalID());
//					if (prevelem == null)
//						continue;
//					prevelem.nextElement.add(block);
//					block.prevElement.add(prevelem);
//					
//					Connection newCon = new Connection(prevelem.LocalID, (prevelem.type == Element.BLOCK) ? (conn.getFormalParam())
//							: (prevelem.invar.getExpression()), block.LocalID, inVar.getFormalParameter());
//					if (inVar.isNegated()) {
//						newCon.negated = true;
//						CreateGUI.consolePrint(" ~ ");
//					}
//					
//					CircuitInfo.connections.add(newCon);
//					prevelem.FormalParam = inVar.getFormalParameter();
//					
//					if (prevelem.type == Element.BLOCK) {
//						CreateGUI.consolePrintln(prevelem.LocalID + prevelem.block.getTypeName() + " <-> " + block.LocalID + " "
//								+ block.block.getTypeName());
//						CreateGUI.consoleFlush();
//					} else if (prevelem.type == Element.INVAR) {
//						CreateGUI.consolePrintln(prevelem.LocalID + " " + prevelem.invar.getExpression() + " <-> " + block.LocalID + " "
//								+ block.block.getTypeName());
//					}
//				}
//			}
//			for (Element outVar : CircuitInfo.IPs) {
//				for (IConnection conn : outVar.outvar.getConnectionPointIn().getConnections()) {
//					Element prevElem = CircuitInfo.getElementByID(conn.getRefLocalID());
//					if (prevElem == null)
//						continue;
//					if(prevElem.block != null) {
//						Connection newCon = new Connection(prevElem.LocalID, conn.getFormalParam(), outVar.LocalID, outVar.outvar.getExpression());
//						if (outVar.outvar.isNegated()) {
//							newCon.negated = true;
//							CreateGUI.consolePrint(" ~ ");
//							CreateGUI.consoleFlush();
//						}
//						CircuitInfo.connections.add(newCon);
//						CreateGUI.consolePrintln("Block" + prevElem.block.getInstanceName() + ": ");
//						CreateGUI.consolePrintln(conn.getFormalParam() + " / " + outVar.outvar.getExpression());
//						
//						CreateGUI.consolePrintln(prevElem.LocalID + " " + prevElem.block.getInstanceName() + " <-> " + outVar.LocalID + " " + outVar.outvar.getExpression());
//						prevElem.nextElement.add(outVar);
//						outVar.prevElement.add(prevElem);
//					}
//				}
//			}
//			
//			CreateGUI.consolePrintln("==========Load circuit info complete===========");
//		}
	}
	
	
	public DrawPanel drawPicture(DrawPanel dp, String IPname) {
		ImageIcon icon = new ImageIcon("D:\\Users\\user\\workspace\\StatCircuitAnalyzer\\StatCircuitAnalyzer\\SAMSUNG_PROJ_EXAMPLE2.png");
		Dimension d = dp.getSize();
		dp.g2d.drawImage(icon.getImage(), 0, 0, d.width*4/5, d.height/6, null);
		
//		List<ClockNetSet> clockNetSets = new ArrayList<ClockNetSet>(); 
////		if(IPname.equals("")) {
////			dp.g2d.setColor(Color.white);
////			dp.g2d.fillRect(0, 0, 10000, 20000);
////		}
//		if(!IPname.equals("")) {
//			for(Element selectedIP : CircuitInfo.IPs){
//				if(selectedIP.name.equals(IPname)) {
//					clockNetSets = selectedIP.clockNetSets;
//					break;
//				}
//			}
//		}
//		
//		if (!IPname.equals("") && clockNetSets == null) {
//			//In case that there if no such IP name.
//			CreateGUI.consolePrintln("No such IP");
//			CreateGUI.consoleFlush();
//			return dp;
//		}
////		} else if (!IPname.equals("") && clockNetSets != null) {
////			//There are clockNet Sets
////		} else if (IPname.equals("")) {
////			//first open 
//		if(IPname.equals("")) {
//			dp.g2d.setColor(Color.white);
//			dp.g2d.fillRect(0, 0, 10000, 20000);
//		}
//			
//		IPOU POU = PLCProject.getPOUs().get(indexOfTopModule);
//		FBD ld = (FBD) POU.getBody();
//		
//		//draw IPs
//			for(IOutVariable IP : CircuitInfo.IPs) {
//				String IPname = IP.name;
//				drawChar(dp, IPname.toCharArray(), 0, IPname.lenght(), IP.getPosition().getX(), IP.getPosition().getY() -3);
//			}
//			
//			//draw blocks
//			for(Element block : CircuitInfo.blocks) {
//				String blockName = block.name;
//				dp.g2d.setColor(Color.red);
//				drawRect(dp, block.getPosition().getX(), block.getPosition().getY(), block.getSize().getWidth(), block.getSize().getHeight() + 25);
//				dp.g2d.setColor(Color.black);
//				drawChar(dp, blockName.toCharArray(), 0, blockName.length(), block.getPosition().getX() + 2, block.getPosition().getY() + 11);
//				int inVarCnt = 0;
//				for(Element in : block.getInVariables()) {
//					String formalParam = in.getFormalParameter();
//					drawChar(dp, formalParam.toCharArray(), 0, formalParam.length(), block.getPosition().getX() + 3, block.getPosition().getY() + 30 + inVarCnt * 30 + 6);
//					inVarCnt++;
//				}
//				int outVarCnt = 0;
//				for(Element out : block.getOutVariables()) {
//					String formalParam = out.getFormalParameter();
//					drawChar(dp, formalParam.toCharArray(), 0, formalParam.length(), block.getPosition().getX() + 3, block.getPosition().getY() + 30 + outVarCnt * 30 + 6);
//					outVarCnt++;
//				}
//			}
//			
//			//draw connections
//			for(Element block : CircuitInfo.blocks) {
//				Element nextBlock = getElementById(getLocalID());
//				for (Element in : block.getInVariables()) {
//					for(Element conn : in.getConnections()) {
//						drawConnection(dp, nextelem, conn, null, 0, 0);
//					}
//				}
//			}
//			
//			for(Element outVar : CircuitInfo.IPs) {
//				Element nextElem = getElementById(outVar.getLocalID());
//				for (Element conn : outVar.getConnections()) {
//					drawConnection(dp, nextelem, conn, null, 0, 0);
//				}
//			}
//			
//			for(Element inVar : CircuitInfo.inputs) {
//				String inputName = inVar.name;
//				dp.g2d.setColor(Color.black);
//				drawChar(dp, chars.toCharArray(), 0, chars.length(), inVar.getPosition().getX(), inVar.getPosition().getY() - 3);
//			}
//		}
		
		return dp;
	}
	
	public void drawChar (DrawPanel dp, char[] data, int offset, int length, int x, int y) {
		dp.g2d.drawChars(data, offset, length, x, y);
		extendPanelSize(dp, x + 10 * length, y + 20);
	}
	
	public void extendPanelSize(DrawPanel dp, int x, int y) {
		Dimension current = dp.getPreferredSize();
		if(current.width < x)
			current.width = x;
		if(current.height > y)
			current.width = y;
		dp.setPreferredSize(current);
	}
	
	public void drawRect (DrawPanel dp, int x, int y, int width, int height) {
		dp.g2d.drawRect(x, y, width, height);
		extendPanelSize(dp, x + width, y + height);
	}
	
	public void drawLine(DrawPanel dp, int x1, int y1, int x2, int y2) {
		dp.g2d.drawLine(x1, y1, x2, y2);
		extendPanelSize(dp, x1, y1);
		extendPanelSize(dp, x2, y2);
	}
	
	public void drawConnection(DrawPanel dp, Element nextElem, Element conn, boolean isSelectedIP, int x, int y ) {
//		if(!isSelectedIP)
//			dp.g2d.setColor(Color.blue);
//		else
//			dp.g2d.setColor(Color.green);
//		
//		long firstid = conn.getRefLocalID();
//		long secondid = nextelem.LocalID;
//		
//		Element prevelem = getElementById(conn.getRefLocalID());
//		IPosition before = null;
//		
//		float hue = (float) 0.65;
//		float saturation = (float) (0.5 + (Math.random() * 0.5));
//		float brightness = (float) (0.0 + (Math.random() * 1.0));
//		dp.g2d.setColor(Color.getHSBColor(hue, saturation, brightness));
//		
//		for (IPosition current : conn.getPositions()) {
//			if (before != null) {
//				if (before.getX() == current.getX() || before.getY() == current.getY()) {
//					drawLine(before.getX() + x, before.getY() + y, current.getX() + x, current.getY() + y);
//				} else {
//					dp.g2d.drawRect(before.getX() - 1 + x, before.getY() - 31 + y, 2, 2);
//					drawLine(before.getX() + x, before.getY() + y, before.getX() + x, before.getY() - 30 + y);
//				}
//			} else {
//				dp.g2d.drawRect(current.getX() - 1 + x, current.getY() - 1 + y, 2, 2);
//			}
//			before = current;
//		}
//		if (before != null) {
//			drawLine(before.getX() + x, before.getY() + y, before.getX() - 80 + x, before.getY() + y);
//			dp.g2d.drawRect(before.getX() - 81 + x, before.getY() - 1 + y, 2, 2);
//		} else {
//			CreateGUI.console_println("Error: No graphical information detected in Connection between " + prevelem.LocalID + " and "
//					+ nextElem.LocalID);
//			if (prevelem.type == Element.INVAR) {
//				drawLine(prevelem.invar.getPosition().getX(), prevelem.invar.getPosition().getY(), nextElem.block.getPosition().getX(),
//						nextElem.block.getPosition().getY() + 1 * nextElem.block.getSize().getHeight() / 2);
//			} else if (nextElem.type == Element.OUTVAR) {
//				drawLine(prevelem.block.getPosition().getX() + prevelem.block.getSize().getWidth(), prevelem.block.getPosition().getY()
//						+ prevelem.block.getSize().getHeight() / 2, nextElem.outvar.getPosition().getX(), nextElem.outvar.getPosition()
//						.getY());
//			} else {
//				drawLine(prevelem.block.getPosition().getX() + prevelem.block.getSize().getWidth(), prevelem.block.getPosition().getY()
//						+ prevelem.block.getSize().getHeight() / 2, nextElem.block.getPosition().getX(), nextElem.block.getPosition()
//						.getY() + 1 * nextElem.block.getSize().getHeight() / 2);
//			}
//			CreateGUI.consoleFlush();
//		}
//		dp.g2d.setColor(Color.black);
	}
}
