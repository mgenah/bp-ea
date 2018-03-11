package il.ac.bgu.cs.ea.gp;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;

public class Mul extends GPNode {
	@Override
	public String toString() {
		return "*";
	}

	public int expectedChildren() { return 2; }

    public void eval(final EvolutionState state,
        final int thread,
        final GPData input,
        final ADFStack stack,
        final GPIndividual individual,
        final Problem problem){
        double result;
        RobocodeEvaluationData ed = ((RobocodeEvaluationData)(input));

        children[0].eval(state,thread,input,stack,individual,problem);
        result = ed.data;

        children[1].eval(state,thread,input,stack,individual,problem);
        ed.data = result * ed.data;
    }

}
