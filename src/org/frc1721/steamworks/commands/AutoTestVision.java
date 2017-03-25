package org.frc1721.steamworks.commands;


import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *
 */
public class AutoTestVision extends CommandGroup {
	public AutoTestVision() {
		addSequential(new SetCoordinates(0.0, 0.0));
		addSequential(new SetYawOffset(180.0));
		addParallel(new ProcessCameraData());
		addSequential(new DrivePause(180.0));
	}


}
