package org.frc1721.steamworks.commands;


import org.frc1721.steamworks.RobotMap;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *
 */
public class AutoCrossLineStraight extends CommandGroup {
	public AutoCrossLineStraight(double startY) {
		addSequential(
				new SetCoordinates(0.0, startY));
		addSequential(new SetYawOffset(0.0));
		addSequential(new EnableDrivePIDCommand());
		addSequential(new TurnAbsolute(0.0, 1));
		addSequential(new DistanceDriveStraight(14.0, 4.0, true));
		// First block to try

	}


}
