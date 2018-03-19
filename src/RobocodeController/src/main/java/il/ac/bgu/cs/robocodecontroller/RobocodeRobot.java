package il.ac.bgu.cs.robocodecontroller;

public enum RobocodeRobot {
	SittingDuck("sample.SittingDuck"),
	Tracker("sample.Tracker"),
	BPjsRobot("il.ac.bgu.cs.bp.bpjsrobot.BPjsRobot*");
	
	private String fullName;

	RobocodeRobot(String fullName){
		this.fullName = fullName;
	}
	
	public String getFullName(){
		return fullName;
	}
}
