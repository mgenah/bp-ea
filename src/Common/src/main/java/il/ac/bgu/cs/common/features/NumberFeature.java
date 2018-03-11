package il.ac.bgu.cs.common.features;

public class NumberFeature extends Feature {
	private double value;

	public NumberFeature(String name, double value) {
		super(name);
		this.value = value;
	}

	public double getValue() {
		return value;
	}
}
