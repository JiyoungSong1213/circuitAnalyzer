package circuitRelated;

public class InVariable {
	
	public static final int OSCILATOR = 0;
	public static final int INPUT = 1;
	
	private String inputName;
	private int inputType;
	private double inputValue;
	private long ID;
	private int IDwidth;
	private int IDheight;
	
	public void setIDwidth (int w) {
		this.IDwidth = w;
	}
	
	public int getIDwidth () {
		return IDwidth;
	}
	
	public void setIDheight (int h) {
		this.IDheight = h;
	}
	
	public int getIDheight () {
		return IDheight;
	}
	
	public void setInitialLocalID (long ID) {
		
	}
	
	public long getLocalID () {
		return ID;
	}
	
	public String getExpression () {
		return inputName;
	}
	
	public int getType () {
		return inputType;
	}
	
	public void setExpression(String inputName) {
		this.inputName = inputName;
	}
	
	public void setType (int type) {
		this.inputType = type;
	}
	
	public double getValue () {
		return inputValue;
	}
	
	public void setValue (double inputVal) {
		this.inputValue = inputVal;
	}
}
