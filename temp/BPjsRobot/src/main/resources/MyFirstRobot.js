importPackage(Packages.il.ac.bgu.cs.bp.bpjsrobot);
importPackage(Packages.il.ac.bgu.cs.bp.bpjsrobot.events.actions);
importPackage(Packages.il.ac.bgu.cs.bp.bpjsrobot.events.sensors);
importPackage(Packages.robocode.util);

veryFar = 9999.0;
quarterTurn = 90.0;
halfTurn = 180.0;
threeQuarterTurn = 270.0;
fullTurn = 360.0;


var targetflag=false;
var targetBear=null;
var firsttimescanned=0;
var targetIsDead=false;
var moveDirection = 1;

var waves = [];
var stats = []; // 31 is the number of unique GuessFactors we're using
// Note: this must be odd number so we can get
// GuessFactor 0 at middle.
var statsLength = 31;

for(var is = 0; is < statsLength; is++) {
	stats.push(0);
}

var direction = 1;

var Scaned = bp.EventSet('', function(e) {
	return (e instanceof ScannedRobot);
});

var Motiend = bp.EventSet('', function(e) {
	return (e instanceof Status) && e.getStatus().getDistanceRemaining() == 0;
});

var Revend = bp.EventSet('', function(e) {
	return (e instanceof Status) && e.getStatus().getTurnRemaining() == 0;
});

var GunRadRevend = bp.EventSet('', function(e) {
	return (e instanceof Status) && e.getStatus().getGunTurnRemainingRadians() == 0;
});

var GunRevend = bp.EventSet('', function(e) {
	return (e instanceof Status) && e.getStatus().getGunTurnRemaining() == 0;
});

var RadRevend = bp.EventSet('', function(e) {
	return (e instanceof Status) && e.getStatus().getRadarTurnRemaining() == 0;
});

var HitWall = bp.EventSet('', function(e) {
	return (e instanceof BpHitWallEvent);
});

var RobotDeath = bp.EventSet('', function(e) {
	return (e instanceof BpRobotDeathEvent);
});


bp.registerBThread("SmartRadarSpin",function() {
	while (true){
		if (targetIsDead){
			break;
		}
		
		if (targetflag==false){
		    bp.log.info('Running radar scan false');
			bsync({ request : TurnRadarRight(10)});
			bsync({ waitFor : RadRevend });
			bp.log.info('radar stopped moving');
		}
		if (targetflag==true){
			bp.log.info('Running radar scan true');
			if (firsttimescanned == 0){
				if (targetBear != null){
					bsync({ request : TurnRadarLeft(targetBear)});
					bsync({ waitFor : RadRevend });
					firsttimescanned=1;
				}
			}
			bsync({ request : TurnRadarLeft(10)});
			bsync({ waitFor : RadRevend });
			//bsync({ request : TurnRadarRight(50)});
			//bsync({ waitFor : RadRevend });
		}
	}
});

bp.registerBThread("IsThereAny Target",function() {
	var bear=null;
	var oldbear=null;
	while(true){
		if (targetIsDead){
			break;
		}
		var e = bsync({ waitFor : Scaned });
		
		bp.log.info('Any target b-thread: found target');
		var bear=e.getData().getBearing();
		if (bear!=null && oldbear!=bear){
			targetflag=true;
			bp.log.info('flagtrue'+targetflag);
			oldbear=bear;
		}
	}
});


bp.registerBThread("Go To Target",function() {
	while(true){
		bp.log.info('Running go for target b-thread');
		var e = bsync({ waitFor : Scaned });	
	    targetBear=e.getData().getBearing();
		var targetDist=e.getData().getDistance();
		bp.log.info("targetBear="+targetBear);
		bp.log.info("targetDist="+targetDist);
		
		bsync({ request : TurnRight(targetBear)});
		bsync({ waitFor : Revend });
		bsync({ request : Back(10) });
		bsync({ waitFor : Motiend });
		bsync({ request : Ahead(targetDist) });
		bsync({ waitFor : Motiend });
	}
});

/*bp.registerBThread("Ram target", function() {
	while(true){
		bp.log.info('Running ram target b-thread');
		var e = bsync({ waitFor : Scaned });	
	    targetBear=e.getData().getBearing();
		var targetDist=e.getData().getDistance();
		bp.log.info("targetBear="+targetBear);
		bp.log.info("targetDist="+targetDist);
		
		bsync({ request : TurnRight(targetBear)});
		bsync({ waitFor : Revend });
		bsync({ request : Back(10) });
		bsync({ waitFor : Motiend });
		bsync({ request : Ahead(targetDist) });
		bsync({ waitFor : Motiend });
	}
});*/

bp.registerBThread("Aim Target",function() {
	var e = bsync({ waitFor : Scaned });	
    targetBear=e.getData().getBearing();
	var targetDist=e.getData().getDistance();
	bp.log.info("targetBear="+targetBear);
	bp.log.info("targetDist="+targetDist);
	
	bsync({ request : TurnRight(targetBear)});
	bsync({ waitFor : Revend });
});

/*bp.registerBThread("Guess Factor Gun", function() {
	while (true) {
		if (targetIsDead){
			break;
		}
		
		var e = bsync({ waitFor : Scaned });
		bp.log.info('Running Guess factor gun b-thread');

//		var statusEvent = e.getStatus();
//		if (statusEvent == null){
//			bp.log.info('no status event');
//			continue;
//		}
		
		var absBearing = e.getData().getHeadingRadians() + e.getData().getBearingRadians();
		 
		// find our enemy's location:
		var ex = robot.getX() + Math.sin(absBearing) * e.getData().getDistance();
		var ey = robot.getY() + Math.cos(absBearing) * e.getData().getDistance();
 
		// Let's process the waves now:
		for (i=0; i < waves.length; i++){
			bp.log.info('current wave: ' + i);
			bp.log.info('waves length: ' + waves.length);
			var currentWave = waves[i];
			bp.log.info('current: ' + currentWave.toString());
			if (currentWave.checkHit(ex, ey, robot.getTime()))
			{
				bp.log.info('got hit');
				waves.splice(i, 1);
				i--;
			}
		}
 
		var power = Math.min(3, Math.max(0.1, 0.2));
		// don't try to figure out the direction they're moving 
		// they're not moving, just use the direction we had before
		if (e.getData().getVelocity() != 0)
		{
			bp.log.info('enemy is moving');
			if (Math.sin(e.getData().getHeadingRadians()-absBearing)*e.getData().getVelocity() < 0)
				direction = -1;
			else
				direction = 1;
		}
		var currentStats = stats; // This seems silly, but I'm using it to
					    // show something else later
		var newWave = new WaveBullet(robot.getX(), robot.getY(), absBearing, power,
                        direction, robot.getTime(), currentStats);
		
		var bestindex = 15;	// initialize it to be in the middle, guessfactor 0.
		for (i=0; i<31; i++)
			if (currentStats[bestindex] < currentStats[i])
				bestindex = i;
 
		// this should do the opposite of the math in the WaveBullet:
		var guessfactor = (bestindex - (stats.length - 1) / 2)
                        / ((stats.length - 1) / 2);
		bp.log.info('guess factor' + guessfactor);
		var angleOffset = direction * guessfactor * newWave.maxEscapeAngle();
		bp.log.info('abs bearing' + absBearing);
		bp.log.info('gun heading' + robot.getGunHeadingRadians());
		bp.log.info('before normalization' + (absBearing - robot.getGunHeadingRadians() + angleOffset));
        var gunAdjust = Utils.normalRelativeAngle(absBearing - robot.getGunHeadingRadians() + angleOffset);
        bp.log.info('turning gun' + gunAdjust);
        bsync({ request : TurnGunRightRadians(gunAdjust)});
        bp.log.info('waiting for gun to turn ------------------------');
        bsync({ waitFor : GunRadRevend });
        bp.log.info('gun turned ------------------------');
        bp.log.info('should fire ' + (robot.getGunHeat() == 0 && gunAdjust < Math.atan2(9, e.getData().getDistance())));
        if (robot.getGunHeat() == 0 && gunAdjust < Math.atan2(9, e.getData().getDistance()))
        	var e1 = bsync({ request : Fire(1.0)});
        	if (e1 != null)
        		waves.push(newWave);
	
	}
});*/

bp.registerBThread(function() {
	bp.log.info('Running fire-on-scan b-thread');
	while (true) {
		if (targetIsDead){
			break;
		}
		var e = bsync({ waitFor : Scaned });
		bp.log.info('Continuing fire-on-scan b-thread');
		var energy = e.getData().getEnergy();
		bsync({ request : Fire(50.0) });
	}
});

bp.registerBThread("identify enemy death", function() {
	bp.log.info('Running identify enemy death b-thread');
	bsync({ waitFor : RobotDeath });
	bp.log.info('Continuing identify enemy death b-thread');
	targetIsDead = true;
});

/*bp.registerBThread("Hit Wall", function() {
	bp.log.info('Running hit wall b-thread');
	while(true){
		var battlefieldWidth = robot.getBattlefieldWidth();
		var battlefieldHeight = robot.getBattlefieldHeigth();
		
		var hitWallES = bp.EventSet( "possible eat wall movements", function(evt){
		    return (evt.name.equals("Ahead") || evt.name.equals("Back")) && robot.mightHitWall();
		});
		
		var movementES = bp.EventSet( "Changing directions", function(evt){
		    return (evt.name.equals("Ahead") || evt.name.equals("Back")) && !robot.mightHitWall() || evt.name.equals("TurnLeft") || evt.name.equals("TurnRight");
		});
		
		bsync({block : hitWallES, waitFor: movementES});
	}
});*/