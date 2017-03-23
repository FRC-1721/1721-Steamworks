package org.frc1721.steamworks.commands;

import org.frc1721.steamworks.RobotMap;
import org.frc1721.steamworks.commands.*;
import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *
 */
public class TeleopDepositGear extends CommandGroup {
	public TeleopDepositGear() {

		addSequential(new DriveToGearTarget(5.0, -2.0, 1.0));
		addSequential(new DriveToGearTarget(3.0, -2.0, 1.0));
		addSequential(new DriveToGearTarget(1.0, -1.0, 0.1));
		addSequential(new DrivePause(1.50));
		addSequential(new DistanceDriveStraight(1.0, 0.5));
	}
}
