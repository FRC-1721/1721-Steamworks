package org.frc1721.steamworks.commands;


import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *
 */
public class AutoCrossLineStraight extends CommandGroup {
    public  AutoCrossLineStraight() {
    	// Set the turn scale to lower
    	addSequential(new EnableDrivePIDCommand());
    	addSequential(new TurnAbsolute(0.0, 1));
    	addSequential(new DistanceDriveStraight(14.0,4.0, true));
    	// First block to try

    }
    

}
