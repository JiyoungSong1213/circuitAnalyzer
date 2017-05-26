package circuitRelated;

public class OutVariable {
	public static final int IP = 0;
	public static final int OUTPUT = 1;
	
	private String outputName;
	private int outputType;
	private double outputValue;
	private long ID;
	
	public void setInitialLocalID (long ID) {
		
	}
	
	public long getLocalID () {
		return ID;
	}
	
	public String getExpression () {
		return outputName;
	}
	
	public int getType () {
		return outputType;
	}
	
	public void setExpression(String outputName) {
		this.outputName = outputName;
	}
	
	public void setType (int type) {
		this.outputType = type;
	}
	
	public double getValue () {
		return outputValue;
	}
	
	public void setValue (double outputVal) {
		this.outputValue = outputVal;
	}
}
