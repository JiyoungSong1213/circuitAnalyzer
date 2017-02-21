package statCircuitAnalyzer;

import java.util.Scanner;

import circuitRelated.CircuitInfo;
import circuitRelated.ClockNet;
import circuitRelated.CircuitElement;

public class StatCircuitAnalyzer {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		CreateGUI GUI = new CreateGUI();
		Scanner scanner = new Scanner(System.in);
		LoadXMLFile loadXMLfile = new LoadXMLFile();
		System.out.println("What is the circuit file?");
		String circuitXMLfile = scanner.nextLine();
		loadXMLfile.parseXML(circuitXMLfile);
		loadXMLfile.setBasicInfo();
		
		CircuitAnalyzer circuitAnalyzer = new CircuitAnalyzer();
		circuitAnalyzer.makeClockNets();
		
		System.out.println("Which IP do you want to analyze?");
		String IPname = scanner.nextLine();
		System.out.println("How much the circuit comsumed power when using " +IPname +" IP?");
		String inputTotalPower = scanner.nextLine();
		double totalPower = Double.parseDouble(inputTotalPower);
		System.out.println("How much voltage?");
		String inputVoltage = scanner.nextLine();
		double voltage = Double.parseDouble(inputVoltage);
		
		circuitAnalyzer.makeClockNetList(CircuitInfo.getIPByName(IPname));
		circuitAnalyzer.getFrequencies();
		circuitAnalyzer.calculateCap(totalPower, voltage);
		
		for (ClockNet selectedClockNet : circuitAnalyzer.clockNetSetForSelectedIP) {
			if(selectedClockNet.keyElement.type == CircuitElement.BLOCK && selectedClockNet.keyElement.block.getTypeName().equals("MUX"))
				System.out.print("ClockNet_" +selectedClockNet.keyElement.LocalID + "_" + selectedClockNet.param + ": " + selectedClockNet.capacity);
			if(selectedClockNet.keyElement.type == CircuitElement.BLOCK && selectedClockNet.keyElement.block.getTypeName().equals("DIV"))
				System.out.print("ClockNet_" +selectedClockNet.keyElement.LocalID + ": " + selectedClockNet.capacity);
		}
	}

}
