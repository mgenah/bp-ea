package il.ac.bgu.cs.bp.bpjsrobot.events.sensors;

import il.ac.bgu.cs.bp.bpjs.events.BEvent;

@SuppressWarnings("serial")
public class RobotSensorEvent extends BEvent {

	public RobotSensorEvent(String aName) {
		super(aName);
	}

}
