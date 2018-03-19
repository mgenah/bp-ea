package il.ac.bgu.cs.ea.gp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPIndividual;
import ec.gp.GPProblem;
import ec.gp.koza.KozaFitness;
import ec.simple.SimpleProblemForm;
import ec.util.Parameter;
import il.ac.bgu.cs.robocodecontroller.BattleRunner;
import il.ac.bgu.cs.robocodecontroller.RobocodeRobot;
import il.ac.bgu.cs.robocodecontroller.SummarizedBattleResults;

public class GPRobocodeProblem extends GPProblem implements SimpleProblemForm{
	private static final long serialVersionUID = 1L;
	private static final int NumOfGames = 1;
	private int treeLog;
	private String treeLogPath = "tree.log";

	@Override
	public void setup(EvolutionState state, Parameter base)
    {
        super.setup(state, base);
        
        // verify our input is the right class (or subclasses from it)
        if (!(input instanceof RobocodeEvaluationData))
            state.output.fatal("GPData class must subclass from " + RobocodeEvaluationData.class,
                base.push(P_DATA), null);
        
        
        try {
			treeLog = state.output.addLog(new File(treeLogPath),true);
		} catch (IOException e) {
			state.output.fatal("An IOException occurred while trying to create the log " + 
					treeLogPath + ":\n" + e);
		}
    }
	
	@Override
	public void evaluate(EvolutionState state, Individual ind, int subpopulation, int threadnum) {
        if (!ind.evaluated)
        {
            KozaFitness f = ((KozaFitness)ind.fitness);
            f.setStandardizedFitness(state, calculateFitness(state, (GPIndividual)ind));
            ind.evaluated = true;
        }
	}

	private double calculateFitness(EvolutionState state, GPIndividual ind){
        writeIndividualToFile(state, ind);
		List<Integer> results = runGames(ind);
		int sum = 0;
		for (int i=0 ; i<results.size() ; i++){
			sum += results.get(i);
		}
		return sum;
	}
	
	private void writeIndividualToFile(EvolutionState state, GPIndividual ind) {
		ind.trees[0].printTreeForHumans(state, treeLog);
	}

	private List<Integer> runGames(GPIndividual ind) {
		List<Integer> result = new ArrayList<>(NumOfGames);
		for (int i=0 ; i<NumOfGames ; i++){
			result.add(runGame(ind));
		}
		return result;
	}

	private Integer runGame(GPIndividual ind) {
		BattleRunner runner = new BattleRunner();
		SummarizedBattleResults results = runner.runBattle(RobocodeRobot.Tracker, RobocodeRobot.SittingDuck);
		
		// TODO: BPjs robot is not in the repository - needs to be fixed.
		System.out.println(results.Results);
		if (results.Results.get(RobocodeRobot.Tracker.getFullName()) > results.Results.get(RobocodeRobot.SittingDuck.getFullName())){
			return 1;
		}
		return 0;
	}

}
