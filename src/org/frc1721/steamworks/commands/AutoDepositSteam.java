package org.frc1721.steamworks.commands;

import org.frc1721.steamworks.RobotMap;
import org.frc1721.steamworks.commands.*;
import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *
 */
public class AutoDepositSteam extends CommandGroup {
    public  AutoDepositSteam(double startX, double startY, int team, boolean dumpBalls) {
    	double hopperDir = 1.0;
    	// Set the intial position.  X is away from driver station, y is to the right.  
    	// 0,0 should be where the hopper is (intersection of the two walls, ignoring diagonal
    	// The blue team hopper is to the left, so all y values are positive
    	if (team == RobotMap.redTeam) hopperDir = -1.0;
    	addSequential(new SetTeam(team));
    	addSequential(new SetYawOffset(0.0));
    	// Set the position
    	addSequential(new SetCoordinates(startX, startY));
    	addSequential(new EnableDrivePIDCommand());
    	addSequential(new TurnAbsolute(0.0, 2));
    	addSequential(new DistanceDriveStraight(3.0,3.0, true));
    	// Drive to a point diagonal from hopper
    	double targetX, targetY;
    	targetX = RobotMap.hopperX + 3.0;
    	targetY = RobotMap.hopperY + hopperDir*3.0;
    	addSequential(new DriveToCoordinates(targetX,targetY, 4.0));
    	
    	// Try the above before adding the next steps
    	if (dumpBalls) {
    		// Drive slowly, may want to turn off gyro
    		addSequential(new TurnAbsolute(-hopperDir*135.0, 5));
    		addSequential(new DistanceDriveStraight(3.0, 1.0, true));
    		// Raise the lift
    		addSequential(new LiftUp());
    	} else {
    		addSequential(new TurnAbsolute(0, 5));
    		addSequential(new DistanceDriveStraight(8.0,4.0, true));
    	}
    }
    

}
