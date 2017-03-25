package org.frc1721.steamworks.commands;

import org.frc1721.steamworks.RobotMap;
import org.frc1721.steamworks.commands.*;
import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *
 */
public class AutoDepositGearSides extends CommandGroup {
	public AutoDepositGearSides(double dir) {

		boolean useOldMethod = true;
		// use a dir of 0 to signal to not j-hook
		if (useOldMethod) {
			addSequential(
					new SetCoordinates(RobotMap.sideStartX, dir*RobotMap.sideStartY));
			addSequential(new SetYawOffset(180.0));
			addSequential(new EnableDrivePIDCommand());
			addSequential(new DistanceDriveStraight(4.5,-4.0, 0.5));
			double targetX = RobotMap.sideGearDepositX - 1.0*0.866;
			double targetY = dir*(RobotMap.sideGearDepositX - 1.0*0.866);
			// Drive to a point in line with the gear deposit
			addSequential(new DriveToCoordinates(targetX, 0.0, -2.0, 0.1, 20));
			
			// targetX = RobotMap.cGearDepositX - 1.0;
			// Drive slowly final portion, increase tolerance to give it time on target
			// addSequential(new DriveToCoordinates(targetX, 0.0, -1.0, 0.1, 20));
			addSequential(new DrivePause(5.0));
			// addSequential(new DistanceDriveStraight(1.0, 0.5, 0.2));
			// addSequential(new DriveToCoordinates(targetX, 0.0, -1.0, 0.1, 20));
			// addSequential(new DrivePause(1.5));
			addSequential(new DistanceDriveStraight(2.0, 0.5));
			if (dir != 0.0) {
				targetX = 14.0;
				targetY = RobotMap.sideStartY*dir;
				addSequential(new DriveToCoordinates(targetX, targetY, 5.0, 2.0, 2));
			}
		} else {
			// Todo Below not working yet.
			// New method that tries to auto-determine the closest target
			// Set the position
			addSequential(
					new SetCoordinates(RobotMap.centerStartX, RobotMap.centerStartY));
			addSequential(new SetYawOffset(180.0));
			addSequential(new EnableDrivePIDCommand());
			double[] approachPoints = new double[2];
			approachPoints[0] = 3.0;
			approachPoints[1] = 1.0;
			addSequential(new DriveToGearTarget(approachPoints, -2.0, 1.0));
			addSequential(new DrivePause(1.50));
			addSequential(new DistanceDriveStraight(1.0, 0.5));
			if (dir != 0.0) {
				addSequential(new DistanceDriveStraight(3.0, 4.0));
				double targetY = dir * RobotMap.quarterFieldWidth;
				addSequential(new DriveToCoordinates(5.0, targetY, 5.0, 2.0, 2));
				addSequential(new DriveToCoordinates(14.0, targetY, 5.0, 2.0, 2));
			}
		}

	}
}
