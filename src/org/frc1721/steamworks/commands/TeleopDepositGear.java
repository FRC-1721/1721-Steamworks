package org.frc1721.steamworks.commands;

import org.frc1721.steamworks.RobotMap;
import org.frc1721.steamworks.commands.*;
import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *
 */
public class TeleopDepositGear extends CommandGroup {
    public  TeleopDepositGear() {
    	double[] approachPoints = new double[2];
		approachPoints[0] = 3.0;
		approachPoints[1] = 1.0;
    	addSequential(new DriveToGearTarget(approachPoints, -3.0, 1.0));
    	addSequential(new DrivePause(4.0));
    	addSequential(new DistanceDriveStraight(1.0, 0.5));
    }
}
