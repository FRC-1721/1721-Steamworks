package org.frc1721.steamworks.commands;


import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *
 */
public class AutoTestVision extends CommandGroup {
    public  AutoTestVision() {
    	// Set the turn scale to lower
    	addSequential(new EnableDrivePIDCommand());
    	addSequential(new SetDistanceToTarget(1.0,1.0));
    	// First block to try

    }
    

}
