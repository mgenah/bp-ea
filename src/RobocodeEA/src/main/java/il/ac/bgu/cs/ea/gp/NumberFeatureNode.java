package il.ac.bgu.cs.ea.gp;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import il.ac.bgu.cs.common.features.NumberFeature;

public class NumberFeatureNode extends FeatureNode {

	private final NumberFeature feature;
	private double weight;
	private final int maxValue;
	private final int minValue;
	
	public NumberFeatureNode(){
		this.feature = new NumberFeature("some feature", 0.0);
		this.maxValue = 0;
		this.minValue = 0;
	}
	
	public NumberFeatureNode(NumberFeature feature, int maxValue, int minValue){
		this.feature = feature;
		this.maxValue = maxValue;
		this.minValue = minValue;
	}
	
	public int expectedChildren() { return 0; }
	
	@Override
	public String toString() {
		return feature.getName() + "*" + weight;
	}

	@Override
	public void eval(EvolutionState state, int thread, GPData input, ADFStack stack, GPIndividual individual,
			Problem problem) {
		((RobocodeEvaluationData)input).data = weight * feature.getValue();
	}	
}
