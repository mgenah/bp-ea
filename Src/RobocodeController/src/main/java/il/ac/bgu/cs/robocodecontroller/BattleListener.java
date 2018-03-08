package il.ac.bgu.cs.robocodecontroller;

import robocode.BattleResults;
import robocode.control.events.BattleAdaptor;
import robocode.control.events.BattleCompletedEvent;
import robocode.control.events.BattleErrorEvent;
import robocode.control.events.BattleMessageEvent;

public class BattleListener extends BattleAdaptor {
	private BattleResults[] results = new BattleResults[0];
	
	public void onBattleCompleted(BattleCompletedEvent e) {
        System.out.println("Battle results:");
        for (BattleResults result : e.getSortedResults()) {
            System.out.println("  " + result.getTeamLeaderName() + ": " + result.getScore());
        }
        results = e.getSortedResults();
    }

    public void onBattleMessage(BattleMessageEvent e) {
        System.out.println("Msg> " + e.getMessage());
    }

    public void onBattleError(BattleErrorEvent e) {
        System.out.println("Err> " + e.getError());
    }
    
    public SummarizedBattleResults getBattleResults(){
    	return new SummarizedBattleResults(results);
    }
}
