package statCircuitAnalyzer;

import java.io.IOException;
import java.util.Scanner;

import circuitRelated.CircuitInfo;
import circuitRelated.ClockNet;
import circuitRelated.CircuitElement;
import circuitRelated.Connection;

public class StatCircuitAnalyzer {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		CreateGUI GUI = new CreateGUI();
//		Scanner scanner = new Scanner(System.in);
//		LoadXMLFile loadXMLfile = new LoadXMLFile();
//		System.out.println("What is the circuit file?");
//		String circuitXMLfile = scanner.nextLine();
//		loadXMLfile.parseXML(circuitXMLfile);
		
//		loadXMLfile.setBasicInfo(circuitXMLfile);
		
//		CircuitAnalyzer circuitAnalyzer = new CircuitAnalyzer();
//		circuitAnalyzer.makeClockNets();
//
//		
//		circuitAnalyzer.makeClockNetList(CircuitInfo.getIPByName("CCLK_CA7_ABOX_TOP"));
//		circuitAnalyzer.getFrequencies();
//		circuitAnalyzer.calculateCap(totalPower, voltage);
//		
//		for (ClockNet selectedClockNet : circuitAnalyzer.clockNetSetForSelectedIP) {
//			if(selectedClockNet.keyElement.type == CircuitElement.BLOCK && selectedClockNet.keyElement.block.getTypeName().equals("MUX"))
//				System.out.println("ClockNet_" +selectedClockNet.keyElement.LocalID + "_" + selectedClockNet.param + ": " + selectedClockNet.power);
//			if(selectedClockNet.keyElement.type == CircuitElement.BLOCK && selectedClockNet.keyElement.block.getTypeName().equals("DIV"))
//				System.out.print("ClockNet_" +selectedClockNet.keyElement.LocalID + ": " + selectedClockNet.capacity);
//		}
//		
//		System.out.println("Total power: " + circuitAnalyzer.totalPower);
	}

}
