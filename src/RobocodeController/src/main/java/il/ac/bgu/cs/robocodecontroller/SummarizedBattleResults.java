package il.ac.bgu.cs.robocodecontroller;

import java.util.HashMap;
import java.util.Map;

import robocode.BattleResults;

public class SummarizedBattleResults {
	public Map<String, Integer> Results = new HashMap<>();
	
	public SummarizedBattleResults(BattleResults[] results){
		for (BattleResults result : results){
			Results.put(result.getTeamLeaderName(), result.getScore());
		}
	}
}
