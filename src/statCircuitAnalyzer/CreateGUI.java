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
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import circuitRelated.CircuitElement;
import circuitRelated.CircuitFreqInfo;
import circuitRelated.CircuitInfo;
import circuitRelated.CircuitUsageMapInfo;
import circuitRelated.ClockNet;
import circuitRelated.Connection;
import userPatternParser.Parser;

public class CreateGUI extends Frame implements ActionListener {
	FileDialog fileDialog;
	DrawPanel[] XMLPanel;
	LoadXMLFile loadXMLfile;
	CircuitMapping circuitMapping;
	JFrame window;
	Container content;
	int defaultWindowWidth = 3000;
	int defualtWindowHeight = 960;
	JTextField filePath;
	JButton openButton;
	JTextField patternPath;
	JButton patternButton;
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
	int cntFile;

	public CreateGUI() {
		window = new JFrame();
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setSize(defaultWindowWidth, defualtWindowHeight);
		window.setPreferredSize(new Dimension(defaultWindowWidth, defualtWindowHeight));
		window.setTitle("StatCircuitAnalyzer");
		window.setVisible(true);

		content = window.getContentPane();
		content.setLayout(new BorderLayout());

		filePath = new JTextField(10);

		openButton = new JButton("Open");
		openButton.addActionListener(this);
		openButton.setActionCommand("openXML");

		patternPath = new JTextField(7);
		
		patternButton = new JButton("Open");
		patternButton.addActionListener(this);
		patternButton.setActionCommand("openPattern");
		
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

		xmlStatus = new JLabel("");

		JPanel menuPanel = new JPanel();
		menuPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		menuPanel.setPreferredSize(new Dimension(260, 600));
		menuPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

		menuPanel.add(new JLabel("         ===== Setting Analyzer =====     "));
		menuPanel.add(new JLabel("XML file: "));
		menuPanel.add(filePath);
		menuPanel.add(openButton);
		
		menuPanel.add(new JLabel("User-patterns: "));
		menuPanel.add(patternPath);
		menuPanel.add(patternButton);

		menuPanel.add(new JLabel("Circuit Library: "));
		libraryPath = new JTextField(7);
		menuPanel.add(libraryPath);
		libraryButton = new JButton("Open");
		libraryButton.addActionListener(this);
		libraryButton.setActionCommand("openlibrary");
		menuPanel.add(libraryButton);

		logFilePath = new JTextField(10);
		logFilePath.setText("Execution_log.txt");
		menuPanel.add(new JLabel("Log file:"));
		menuPanel.add(logFilePath);
		logButton = new JButton("Select");
		logButton.addActionListener(this);
		logButton.setActionCommand("selectlogfile");
		logButton.setEnabled(true);
		menuPanel.add(logButton);
		
		menuPanel.add(new JLabel("Canvas size : "));
		menuPanel.add(panelSizeX);
		menuPanel.add(new JLabel("x"));
		menuPanel.add(panelSizeY);
		menuPanel.add(setSizeButton);

		JPanel startPanel = new JPanel();
		startPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		startPanel.setPreferredSize(new Dimension(250, 100));
		startPanel.add(analyzeCircuit);
		menuPanel.add(startPanel);
		JScrollPane menuJsp = new JScrollPane(menuPanel);

		JPanel menuXMLpanel = new JPanel();
		menuXMLpanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
		menuXMLpanel.setPreferredSize(new Dimension(450, 225));

		content.add("West", menuJsp);

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
				if (!filePath.getText().equals("")) {
					loadXMLfile = new LoadXMLFile();
					consolePrintln("Reading XML...");
					consoleFlush();
					consolePrintln(filePath.getText());
					StringTokenizer st = new StringTokenizer(filePath.getText(), ";");
					cntFile = st.countTokens();
					for (int i = 0; i < cntFile; i++) {
						String fileName = st.nextToken();
						StatCircuitAnalyzer.circuitInfos[i] = new CircuitInfo();
						loadXMLfile.setBasicInfo(fileName, StatCircuitAnalyzer.circuitInfos[i]);
					}
					circuitAnalyzer = new CircuitAnalyzer();
					consolePrintln("Drawing XML...");
					consoleFlush();

					JTabbedPane jTab = new JTabbedPane();
					XMLPanel = new DrawPanel[cntFile];
					for (int i = 0; i < cntFile; i++) {
						XMLPanel[i] = new DrawPanel();
						XMLPanel[i].setPreferredSize(new Dimension(4000, 2000));
						XMLPanel[i].initPanel(4096, 4096);
						XMLPanel[i].g2d.setColor(Color.black);
						XMLPanel[i] = loadXMLfile.drawPicture(XMLPanel[i], StatCircuitAnalyzer.circuitInfos[i], true);
						JScrollPane XMLjsp = new JScrollPane(XMLPanel[i]);
						jTab.add(StatCircuitAnalyzer.circuitInfos[i].circuitName, XMLjsp);
					}
					content.add("Center", jTab);
				}
				window.repaint();
				window.setVisible(true);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (cmd.equals("openPattern")) {
			try{
				openWindow(2);
				consolePrintln("Opne user-patterns...");
				consoleFlush();
				consolePrintln(patternPath.getText());
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (cmd.equals("analyzeIP")) {
			
		} else if (cmd.equals("analyzeCircuit")) {
			
			StringTokenizer st = new StringTokenizer(patternPath.getText(), ";");
			while(st.hasMoreTokens()) {
				Parser patternParser = new Parser(new File(st.nextToken()));
				try {
					StatCircuitAnalyzer.uLogList.add(patternParser.parseLog());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			
			CircuitAnalyzer circuitAnalyzer = new CircuitAnalyzer();

			consolePrintln("Calculating Consumed Power of Clock Nets in the Circuit ...");
			consoleFlush();

			consolePrintln("");
			consolePrintln("Total power: " + circuitAnalyzer.totalPower + "W");
			consoleFlush();
		} else if (cmd.equals("setcanvassize")) {
			int x = Integer.parseInt(panelSizeX.getText());
			int y = Integer.parseInt(panelSizeY.getText());
			for (int i = 0; i < cntFile; i++) {
				XMLPanel[i].initPanel(x, y);
				XMLPanel[i].setPreferredSize(new Dimension(x, y));
				XMLPanel[i].invalidate();
			}
		} else if (cmd.equals("selectlogfile")) {
			try {
				FileDialog save = new FileDialog(this, "Save Log File", FileDialog.SAVE);
				save.setVisible(true);
				String selectedFilePath = "";

				if (save.getDirectory() != null && save.getFile() != null) {
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
			StringTokenizer st = new StringTokenizer(libraryPath.getText(), ";");
			String libPath = st.nextToken();
			consolePrintln("Reading Library file...");
			consoleFlush();
			consolePrintln(libPath);
			consoleFlush();
			circuitMapping = new CircuitMapping();
			circuitMapping.parseCircuitLib(libPath);
			circuitMapping.mapCircuitLib();

			// Coloring with different color per clock net
			int i = 0;
			for (CircuitInfo ci : StatCircuitAnalyzer.circuitInfos) {
				if(ci == null)
					break;
				circuitAnalyzer.makeClockNets(ci);
				for (ClockNet cn : ci.clockNets) {
					float red = (float) Math.random();
					float green = (float) Math.random();
					float blue = (float) Math.random();
					for (Connection conn : cn.clockNetConnection) {
						loadXMLfile.drawConnection(XMLPanel[i], CircuitInfo.getElementByID(conn.end, ci.circuitElem),
								conn.conn, new Color(red, green, blue), 0, 0, ci);
					}
				}
				i++;
			}
			window.repaint();
			window.setVisible(true);
			
			// remove irrelated clockNet and calculate each clock net's
			// frequency
			circuitAnalyzer.removeIrrelatedClockNetAndCalcEachClockNet();
			System.out.println("end remove");
			// Select used ClockNet for Each usage
			circuitAnalyzer.selectUsedClockNetforEachUsage();
		}
	}

	public void openWindow(int type) throws IOException {
		if (type == 0) {
			fileDialog = new FileDialog(this, "Open XML File", FileDialog.LOAD);
			fileDialog.setFile("*.xml");
			fileDialog.setMultipleMode(true);
		} else if (type == 1) {
			fileDialog = new FileDialog(this, "Open Circuit Library", FileDialog.LOAD);
			fileDialog.setFile("*.txt");
			fileDialog.setMultipleMode(false);
		} else if (type == 2) {
			fileDialog = new FileDialog(this, "Open User-Patterns", FileDialog.LOAD);
			fileDialog.setFile("*.txt");
			fileDialog.setMultipleMode(true);
		}
		fileDialog.setVisible(true);

		String selectedFilePath = "";
		File files[] = fileDialog.getFiles();
		if (files.length != 0) {
			for (File file : files) {
				selectedFilePath += (file.getAbsolutePath() + ";");
			}
		} else {
			selectedFilePath = "";
		}

		if (type == 0) {
			filePath.setText(selectedFilePath);
			consolePrintln("The subject file is selected.");
			consoleFlush();
		} else if (type == 1) {
			libraryPath.setText(selectedFilePath);
			consolePrintln("The circuit lib file is selected.");
			consoleFlush();
		} else if (type == 2) {
			patternPath.setText(selectedFilePath);
			consolePrintln("The user-pattern files are selected.");
			consoleFlush();
		}

		if (!(filePath.getText().equals("") || libraryPath.getText().equals("") || patternPath.getText().equals(""))) {
			analyzeCircuit.setEnabled(true);
		}
	}

	public static void consoleFlush() {
		console.setText(consoleText);
		int position = console.getText().length();
		console.setCaretPosition(position);
		console.requestFocus();
	}

	public static void consolePrint(String addedText) {
		consoleText = consoleText + addedText;
	}

	public static void consolePrintln(String addString) {
		consoleText = consoleText + addString + "\n";
	}

}

class DrawPanel extends JPanel {
	private final long serialVeresionUID = 1L;

	BufferedImage bImage;
	Graphics2D g2d;

	void initPanel(int x, int y) {
		bImage = new BufferedImage(x, y, BufferedImage.TYPE_INT_ARGB);
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
	public void paintComponent(Graphics g) {
		setOpaque(false);
		super.paintComponent(g);
	}

	@Override
	public void update(Graphics g) {
		g.drawImage(bImage, 0, 0, this);
	}
}
