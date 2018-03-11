package il.ac.bgu.cs.ea.ga;

import java.util.ArrayList;
import java.util.List;

import ec.EvolutionState;
import ec.Individual;
import ec.Problem;
import ec.simple.SimpleFitness;
import ec.simple.SimpleProblemForm;
import il.ac.bgu.cs.robocodecontroller.BattleRunner;
import il.ac.bgu.cs.robocodecontroller.RobocodeRobot;
import il.ac.bgu.cs.robocodecontroller.SummarizedBattleResults;

public abstract class RobocodeProblem extends Problem implements SimpleProblemForm {
	private static final long serialVersionUID = 1L;
	private static final int NumOfGames = 1;

	@Override
	public void evaluate(EvolutionState state, Individual ind, int subpopulation, int threadnum) {
		if (ind.evaluated) return;

        if (!(ind instanceof RobocodeIndividual))
            state.output.fatal("It's not a RobocodeIndividual!",null);
        
        RobocodeIndividual ind2 = (RobocodeIndividual)ind;
        
        if (!(ind2.fitness instanceof SimpleFitness))
            state.output.fatal("It's not a SimpleFitness!",null);
        ((SimpleFitness)ind2.fitness).setFitness(state,
            calculateFitness((RobocodeIndividual)ind),
            false);
        ind2.evaluated = true;
	}
	
	private double calculateFitness(RobocodeIndividual ind){
		List<Integer> results = runGames(ind);
		int sum = 0;
		for (int i=0 ; i<results.size() ; i++){
			sum += results.get(i);
		}
		return sum;
	}

	private List<Integer> runGames(RobocodeIndividual ind) {
		List<Integer> result = new ArrayList<>(NumOfGames);
		for (int i=0 ; i<NumOfGames ; i++){
			result.add(runGame(ind));
		}
		return result;
	}

	private Integer runGame(RobocodeIndividual ind) {
		BattleRunner runner = new BattleRunner();
		SummarizedBattleResults results = runner.runBattle(RobocodeRobot.BPjsRobot, RobocodeRobot.SittingDuck);
		if (results.Results.get(RobocodeRobot.BPjsRobot.getFullName()) > results.Results.get(RobocodeRobot.BPjsRobot.getFullName())){
			return 1;
		}
		return 0;
	}
}
