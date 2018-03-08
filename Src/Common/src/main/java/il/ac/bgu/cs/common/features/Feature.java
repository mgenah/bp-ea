package il.ac.bgu.cs.common.features;

public abstract class Feature {
	protected String name;

	public Feature(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
