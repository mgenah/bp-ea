package il.ac.bgu.cs.bp.bpjsrobot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Vector;

import org.mozilla.javascript.Scriptable;

import il.ac.bgu.cs.bp.bpjs.bprogram.runtimeengine.BProgramRunner;
import il.ac.bgu.cs.bp.bpjs.bprogram.runtimeengine.SingleResourceBProgram;
import il.ac.bgu.cs.bp.bpjs.eventselection.EventSelectionStrategy;
import il.ac.bgu.cs.bp.bpjsrobot.events.sensors.BpHitWallEvent;
import il.ac.bgu.cs.bp.bpjsrobot.events.sensors.BpRobotDeathEvent;
import il.ac.bgu.cs.bp.bpjsrobot.events.sensors.ScannedRobot;
import il.ac.bgu.cs.bp.bpjsrobot.events.sensors.Status;
import robocode.AdvancedRobot;
import robocode.BattleEndedEvent;
import robocode.HitWallEvent;
import robocode.RobotDeathEvent;
import robocode.ScannedRobotEvent;
import robocode.StatusEvent;
import robocode.WinEvent;

public class BPjsRobot extends AdvancedRobot {
	private static SingleResourceBProgram bprog = new SingleResourceBProgram("MyFirstRobot.js") {
		protected void setupProgramScope(Scriptable scope) {
			putInGlobalScope("robot", this);
			super.setupProgramScope(scope);
		}
	};
	
	static {
		String featureBasedPolicy = null;
		try {
			featureBasedPolicy = new String(Files.readAllBytes(Paths.get("")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		EventSelectionStrategy eventSelectionStrategy = new FeatureBasedEventSelectionStrategy(featureBasedPolicy);
		bprog.setEventSelectionStrategy(eventSelectionStrategy );
	}

	public void run() {
		BProgramRunner runner = new BProgramRunner(bprog); 
		bprog.setDaemonMode(true);
		runner.addListener(new RobocodeEventListener(this));
		
		// go!
		try {
			runner.start();
		} catch (InterruptedException e) {
			e.printStackTrace(out);
		}
		System.out.println("---- done -----");
	}

	@Override
	public void onStatus(StatusEvent e) {
		bprog.enqueueExternalEvent(new Status(e));
	}

	@Override
	public void onScannedRobot(ScannedRobotEvent e) {
		Vector<StatusEvent> statusEvents = getStatusEvents();
		StatusEvent lastStatus = null;
		if (!statusEvents.isEmpty())
			lastStatus = statusEvents.lastElement();
		bprog.enqueueExternalEvent(new ScannedRobot(e, lastStatus));
	}
	
	@Override
	public void onHitWall(HitWallEvent e) {
		bprog.enqueueExternalEvent(new BpHitWallEvent(e));
	}
	
	@Override
	public void onRobotDeath(RobotDeathEvent e){
		bprog.enqueueExternalEvent(new BpRobotDeathEvent(e));
	}
	
	@Override
	public void onWin(WinEvent e){
	}
	
	@Override
	public void onBattleEnded(BattleEndedEvent e){
		bprog.setDaemonMode(false);
	}
	
	public boolean mightHitWall(){
		return true;
	}
}
