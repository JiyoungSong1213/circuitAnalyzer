package statCircuitAnalyzer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import circuitRelated.CircuitInfo;
import circuitRelated.ClockNet;
import circuitRelated.CircuitElement;
import circuitRelated.Connection;
import userPatternParser.UserLog;

public class StatCircuitAnalyzer {
	static CircuitInfo[] circuitInfos = new CircuitInfo[64];
	static double voltage;
	static ArrayList<UserLog> uLogList = new ArrayList<>();
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		  
		CreateGUI GUI = new CreateGUI();
	}

}
