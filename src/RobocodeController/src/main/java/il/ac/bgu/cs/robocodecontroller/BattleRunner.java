package il.ac.bgu.cs.robocodecontroller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import robocode.control.BattleSpecification;
import robocode.control.BattlefieldSpecification;
import robocode.control.RobocodeEngine;
import robocode.control.RobotSpecification;

public class BattleRunner {
	private BattleListener battleListener;
	
	public BattleRunner(){
		battleListener = new BattleListener();
	}
	
	public SummarizedBattleResults runBattle(RobocodeRobot... robocodeRobots){
		System.setProperty("NOSECURITY", "true");
		//TODO: choose workdir from 3rd party
		RobocodeEngine engine = new RobocodeEngine(new java.io.File("C:\\robocode1"));
		engine.addBattleListener(battleListener);
		BattlefieldSpecification battlefield = new BattlefieldSpecification(800, 600);
		
		List<RobocodeRobot> robotsList = Arrays.asList(robocodeRobots);
		String robots = String.join(",", robotsList.stream().map(r -> r.getFullName()).collect(Collectors.toList()));
		RobotSpecification[] robotSpec = engine.getLocalRepository(robots);
//		Print robots in repository:		
//		RobotSpecification[] localRepository = engine.getLocalRepository();
//		for(RobotSpecification r : localRepository){
//			System.out.println(r.getName());
//		}

		// TODO: The battle specification (such as number of rounds) should be received as parameter
		BattleSpecification spec = new BattleSpecification(1, battlefield, robotSpec);
		engine.runBattle(spec);
		engine.waitTillBattleOver();
		engine.close();
		
		return battleListener.getBattleResults();
	}

	public static void main(String[] args){
		BattleRunner runner = new BattleRunner();
		System.out.println(runner.runBattle(RobocodeRobot.BPjsRobot, RobocodeRobot.SittingDuck));
	}
}
