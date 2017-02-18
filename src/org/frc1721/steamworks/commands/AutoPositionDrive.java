package org.frc1721.steamworks.commands;


import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *
 */
public class AutoPositionDrive extends CommandGroup {
    public  AutoPositionDrive() {
    	// Set the turn scale to lower
    	//addSequential(new SetDriveRates(10.0, 45.0));
    	//addSequential(new EnableDrivePIDCommand());
 
    	addSequential(new EnableDrivePIDCommand());
    	
    	// First block to try
//    	addSequential(new DriveToCoordinates(10.0,0.0,2.0));
//    	addSequential(new DriveToCoordinates(0.0,0.0,2.0));
//    	addSequential(new DriveToCoordinates(10.0,0.0,2.0));
//    	addSequential(new DriveToCoordinates(0.0,0.0,2.0));
    	
    	// Second block to try, should back up
    	addSequential(new DriveToCoordinates(8.0, 0.0, 2.0));
    	addSequential(new DriveToCoordinates(8.0, 2.0, 2.0));
    	
    	addSequential(new DisableDrivePIDCommand());
    	
    	

    	
    	
    	
        // Add Commands here:
        // e.g. addSequential(new Command1());
        //      addSequential(new Command2());
        // these will run in order.

        // To run multiple commands at the same time,
        // use addParallel()
        // e.g. addParallel(new Command1());
        //      addSequential(new Command2());
        // Command1 and Command2 will run in parallel.

        // A command group will require all of the subsystems that each member
        // would require.
        // e.g. if Command1 requires chassis, and Command2 requires arm,
        // a CommandGroup containing them would require both the chassis and the
        // arm.
    }
    

}
