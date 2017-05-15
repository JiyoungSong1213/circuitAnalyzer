package statCircuitAnalyzer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import circuitRelated.CircuitElement;
import circuitRelated.CircuitInfo;
import circuitRelated.ClockNet;
import circuitRelated.Connection;

public class CreateGUI extends Frame implements ActionListener{
	FileDialog fileDialog;
	DrawPanel XMLPanel;
	LoadXMLFile loadXMLfile;
	JFrame window;
	Container content;
	int defaultWindowWidth = 1280;
	int defualtWindowHeight = 960;
	JTextField filePath;
	JButton openButton;
//	JButton loadButton;
	JTextField IPField;
	JTextField freqField;
	JButton analyzeIP;
	JButton analyzeCircuit;
	JTextField panelSizeX;
	JTextField panelSizeY;
	JButton setSizeButton;
	JLabel xmlStatus;
	JTextField logFilePath;
	JButton logButton;
	JTextField libraryPath;
	JButton libraryButton;
	static JTextArea console;
	static String consoleText = "";
	CircuitAnalyzer circuitAnalyzer;
	
	public CreateGUI () {
		window = new JFrame();
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setSize(defaultWindowWidth, defualtWindowHeight);
		window.setTitle("StatCircuitAnalyzer");
		window.setVisible(true);
		
		content = window.getContentPane();
		content.setLayout(new BorderLayout());
		
		filePath = new JTextField(10);
		
		openButton = new JButton("Open");
		openButton.addActionListener(this);
		openButton.setActionCommand("openXML");
		
//		loadButton = new JButton("Load XML");
//		loadButton.addActionListener(this);
//		loadButton.setActionCommand("loadXML");
//		loadButton.setEnabled(false);
		
		IPField = new JTextField(10);
		freqField = new JTextField(10);
//		analyzeIP = new JButton("Analyze IP");
//		analyzeIP.addActionListener(this);
//		analyzeIP.setActionCommand("analyzeIP");
//		analyzeIP.setEnabled(false);
		
		analyzeCircuit = new JButton("Analyze circuit");
		analyzeCircuit.addActionListener(this);
		analyzeCircuit.setActionCommand("analyzeCircuit");
		analyzeCircuit.setEnabled(false);
		
		panelSizeX = new JTextField(3);
		panelSizeY = new JTextField(3);
		panelSizeX.setText("1536");
		panelSizeY.setText("4096");
		setSizeButton = new JButton("Set");
		setSizeButton.addActionListener(this);
		setSizeButton.setActionCommand("setcanvassize");
		setSizeButton.setEnabled(false);
		
		xmlStatus = new JLabel("");
		
		JPanel menuPanel =  new JPanel();
		menuPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		menuPanel.setPreferredSize(new Dimension(260, 600));
		menuPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
		
		menuPanel.add(new JLabel("         ===== Setting Analyzer =====     "));
		menuPanel.add(new JLabel("XML file: "));
		menuPanel.add(filePath);
		menuPanel.add(openButton);
		
		logFilePath = new JTextField(10);
		logFilePath.setText("Execution_log.txt");
		menuPanel.add(new JLabel("Log file:"));
		menuPanel.add(logFilePath);
		logButton = new JButton("Select");
		logButton.addActionListener(this);
		logButton.setActionCommand("selectlogfile");
		logButton.setEnabled(true);
		menuPanel.add(logButton);
		
		menuPanel.add(new JLabel("Circuit Library: "));
		libraryPath = new JTextField(7);
		libraryPath.setText("lib\\Calc library_updated_20120105.txt");
		menuPanel.add(libraryPath);
		libraryButton = new JButton("Open");
		libraryButton.addActionListener(this);
		libraryButton.setActionCommand("openlibrary");
		menuPanel.add(libraryButton);

		menuPanel.add(new JLabel("Canvas size : "));
		menuPanel.add(panelSizeX);
		menuPanel.add(new JLabel("x"));
		menuPanel.add(panelSizeY);
		menuPanel.add(setSizeButton);
		
		menuPanel.add(new JLabel("                                      "));
		menuPanel.add(new JLabel("         ===== Execute Analyzer =====   "));
		
		JPanel startPanel = new JPanel();
		startPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		startPanel.setPreferredSize(new Dimension(250, 100));
		startPanel.add(analyzeCircuit);
		menuPanel.add(new JLabel("Selected IP : "));
		menuPanel.add(IPField);
		menuPanel.add(new JLabel("IP frequency: "));
		menuPanel.add(freqField);
//		menuPanel.add(analyzeIP);
		menuPanel.add(startPanel);
		JScrollPane menuJsp = new JScrollPane(menuPanel);
		
		JPanel menuXMLpanel = new JPanel();
		menuXMLpanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
		menuXMLpanel.setPreferredSize(new Dimension(450, 225));
		
		content.add("West", menuJsp);
		
		XMLPanel = new DrawPanel();
		
		XMLPanel.setPreferredSize(new Dimension(1536, 4096));
		JScrollPane XMLjsp = new JScrollPane(XMLPanel);
		
		content.add("Center", XMLjsp);
		
		console = new JTextArea(13, 112);
		console.setFont(new Font("Courier New", Font.PLAIN, 13));
		JScrollPane consoleJsp = new JScrollPane(console);
		menuXMLpanel.add(consoleJsp);
		content.add("South", menuXMLpanel);
		
		window.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				System.out.println(" Resize: width " + window.getWidth() + ", height " + window.getHeight());
				consoleJsp.setPreferredSize(new Dimension(window.getWidth() - 30, 211));
				consoleJsp.setSize(new Dimension(window.getWidth() - 30, 211));
				window.repaint();
				window.setVisible(true);
			}
		});
		
		XMLPanel.initPanel(1536, 4096);
		XMLPanel.g2d.setColor(Color.black);
		
		consoleJsp.setPreferredSize(new Dimension(window.getWidth() - 30, 211));
		
		window.repaint();
		window.setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		String cmd = actionEvent.getActionCommand();
		if (cmd.equals("openXML")) {
			try {
				openWindow(0);
				if(!filePath.getText().equals("")) {
					loadXMLfile = new LoadXMLFile();
					setSizeButton.setEnabled(true);
//					consolePrintln("Loading library...");
//					consoleFlush();
//					loadXMLfile.getLibrary();
					consolePrintln("Reading XML...");
					consoleFlush();
					loadXMLfile.setBasicInfo(filePath.getText());
//					consolePrintln("Creating Circuit Objects ...");
//					consoleFlush();
					circuitAnalyzer = new CircuitAnalyzer();
					consolePrintln("Drawing XML...");
					consoleFlush();
//					String imagePath = "D:\\Users\\user\\workspace\\StatCircuitAnalyzer\\StatCircuitAnalyzer\\SAMSUNG_PROJ_EXAMPLE.png";
//					JLabel label = new JLabel(new ImageIcon(imagePath));
//					label.setHorizontalAlignment(JLabel.CENTER);
//					XMLPanel.add(label);
					
					XMLPanel = loadXMLfile.drawPicture(XMLPanel, "");
//					System.out.println(XMLPanel.a);
				}
				window.repaint();
				window.setVisible(true);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (cmd.equals("analyzeIP")) {
			
		} else if (cmd.equals("analyzeCircuit")) {
			CircuitAnalyzer circuitAnalyzer = new CircuitAnalyzer();
			
			consolePrintln("Calculating Consumed Power of Clock Nets in the Circuit ...");
			consoleFlush();
			circuitAnalyzer.makeClockNets();
			
			circuitAnalyzer.makeClockNetList(CircuitInfo.getIPByName("CCLK_CA7_ABOX_TOP"));
//			circuitAnalyzer.getFrequencies();
//			circuitAnalyzer.calculateCap(totalPower, voltage);
//			
			for (ClockNet selectedClockNet : circuitAnalyzer.clockNetSetForSelectedIP) {
//				if(selectedClockNet.keyElement.type == CircuitElement.BLOCK && selectedClockNet.keyElement.block.getTypeName().equals("MUX"))
					consolePrintln("ClockNet_" +selectedClockNet.keyElement.LocalID + "_" + selectedClockNet.param + ": " + selectedClockNet.power +"W");
					consoleFlush();
//				if(selectedClockNet.keyElement.type == CircuitElement.BLOCK && selectedClockNet.keyElement.block.getTypeName().equals("DIV"))
//					System.out.print("ClockNet_" +selectedClockNet.keyElement.LocalID + ": " + selectedClockNet.capacity);
					System.out.println("ClockNet_" +selectedClockNet.keyElement.LocalID + "_" + selectedClockNet.param + ": " + selectedClockNet.power +"W");
				for(ClockNet cn: CircuitInfo.clockNets) {
					if(cn.keyElement.LocalID == selectedClockNet.keyElement.LocalID) {
							for (Connection con : cn.clockNetConnection) {
								System.out.println(con.start + " "+ con.startParam + " <-> " + con.end + " " + con.endParam);
							}
					}
				}
				System.out.println();
			}
			consolePrintln("");
			consolePrintln("Total power: " + circuitAnalyzer.totalPower+"W");
			consoleFlush();
		} else if (cmd.equals("setcanvassize")) {
			int x = Integer.parseInt(panelSizeX.getText());
			int y = Integer.parseInt(panelSizeY.getText());
			XMLPanel.initPanel(x, y);
			XMLPanel.setPreferredSize(new Dimension(x, y));
//			XMLPanel.silence = true;
//			drawPicture?
			XMLPanel.invalidate();
		} else if (cmd.equals("selectlogfile")) {
			try{
				FileDialog save = new FileDialog(this, "Save Log File", FileDialog.SAVE);
				save.setVisible(true);
				String selectedFilePath = "";
				
				if(save.getDirectory() != null && save.getFile() != null) {
					selectedFilePath = save.getDirectory() + save.getFile();
					openButton.setEnabled(true);
				} else {
					selectedFilePath = "";
					openButton.setEnabled(false);
				}
				logFilePath.setText(selectedFilePath);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (cmd.equals("openlibrary")) {
			try {
				openWindow(1);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public void openWindow (int type) throws IOException {
		if (type == 0) {
			fileDialog = new FileDialog(this, "Open XML File", FileDialog.LOAD);
			fileDialog.setFile("*.xml");
		} else if (type == 1) {
			fileDialog = new FileDialog(this, "Open Calculation Library", FileDialog.LOAD);
			fileDialog.setFile("*.txt");
		}
		
		fileDialog.setVisible(true);
		
		String selectedFilePath = "";
		if(fileDialog.getDirectory() != null && fileDialog.getFile() != null) {
			selectedFilePath = fileDialog.getDirectory() + fileDialog.getFile();
		} else {
			selectedFilePath = "";
		}
		
		if (type == 0) {
			filePath.setText(selectedFilePath);
			consolePrintln("The subject file is selected.");
			consoleFlush();
		} else if (type == 1) {
			libraryPath.setText(selectedFilePath);
		}
		
		if (!(filePath.getText().equals("") || libraryPath.getText().equals(""))) {
//			analyzeIP.setEnabled(true);
			analyzeCircuit.setEnabled(true);
		}
	}
	public static void consoleFlush () {
		console.setText(consoleText);
		int position = console.getText().length();
		console.setCaretPosition(position);
		console.requestFocus();
	}
	
	public static void consolePrint (String addedText) {
		consoleText = consoleText + addedText;
	}
	
	public static void consolePrintln (String addString) {
		consoleText = consoleText + addString + "\n";
	}
	
	
}

class DrawPanel extends JPanel {
	private final long serialVeresionUID = 1L;
	
	int a;
	
	BufferedImage bImage;
	Graphics2D g2d;
	
	void initPanel (int x, int y) {
		bImage = new BufferedImage(x ,y, BufferedImage.TYPE_INT_ARGB);
		g2d = bImage.createGraphics();
		g2d.setBackground(Color.white);
		g2d.setColor(Color.white);
		g2d.fillRect(0, 0, x, y);
		g2d.setColor(Color.black);
	}
	
	@Override
	public void paint(Graphics g) {
		g.drawImage(bImage, 0, 0, this);
	}
	
	@Override
	public void paintComponent (Graphics g) {
		setOpaque(false);
		super.paintComponent(g);
	}
	
	@Override
	public void update (Graphics g) {
		g.drawImage(bImage, 0, 0, this);
	}
}
