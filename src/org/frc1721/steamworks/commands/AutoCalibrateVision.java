package org.frc1721.steamworks.commands;

import org.frc1721.steamworks.RobotMap;
import org.frc1721.steamworks.commands.*;
import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *
 */
public class AutoCalibrateVision extends CommandGroup {
    public  AutoCalibrateVision(double dir) {

    	// Set the position - use a coordinate system centered on gear drop-off.  Robot center is at -1.5 ft.
    	double targetX = -1.5;
    	addSequential(new SetCoordinates(targetX, 0.0));
    	addSequential(new SetYawOffset(180.0));
    	addSequential(new EnableDrivePIDCommand());
    	targetX -= 1.0; // Move backwards a foot first before taking data
		addSequential(new DriveToCoordinates(targetX, 0.0, 0.2, 0.1, 5));
    	addParallel(new CalibrateVision());
    	for (int i = 0; i < 6; i++){
    		targetX -= 1.0; // Move backwards in 1 foot increments.
    		addSequential(new DriveToCoordinates(targetX, 0.0, 0.2, 0.1, 5));
    		addSequential(new TurnAbsolute(5.0, 5, 2));
    		addSequential(new TurnAbsolute(-5.0, 5, 2));
    	}
    	// Drive to a point in line with the gear deposit
    	addSequential(new ProcessCameraData());
    }
}
