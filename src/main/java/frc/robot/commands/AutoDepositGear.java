package org.frc1721.steamworks.commands;

import org.frc1721.steamworks.RobotMap;
import org.frc1721.steamworks.commands.*;
import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *
 */
public class AutoDepositGear extends CommandGroup {
    public  AutoDepositGear(double dir) {

    	// Set the position
    	addSequential(new SetCoordinates(RobotMap.centerStartX, RobotMap.centerStartY));
    	addSequential(new SetYawOffset(180.0));
    	addSequential(new EnableDrivePIDCommand());
    	double targetX = RobotMap.gearDepositX - 5.0;
    	// Drive to a point in line with the gear deposit
    	addSequential(new DriveToCoordinates(targetX, 0.0, -2.0));
    	targetX = RobotMap.gearDepositX - 1.0;
    	// Drive slowly final portion, increase tolerance to give it time on target
    	addSequential(new DriveToCoordinates(targetX, 0.0, -1.0, 0.1, 20));
    	addSequential(new DrivePause(1.50));
    	//addSequential(new DistanceDriveStraight(1.0, 0.5, 0.2));
    	//addSequential(new DriveToCoordinates(targetX, 0.0, -1.0, 0.1, 20));
    	//addSequential(new DrivePause(1.5));
    	addSequential(new DistanceDriveStraight(1.0, 0.5));
    	addSequential(new DistanceDriveStraight(3.0, 4.0));
    	double targetY = dir*RobotMap.quarterFieldWidth;
    	addSequential(new DriveToCoordinates(5.0, targetY, 5.0, 2.0, 2));
    	addSequential(new DriveToCoordinates(14.0,targetY, 5.0, 2.0, 2));
    }
}
