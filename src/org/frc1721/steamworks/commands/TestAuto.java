package org.frc1721.steamworks.commands;


import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *
 */
public class TestAuto extends CommandGroup {
    public  TestAuto() {
    	// Set the turn scale to lower
    	//addSequential(new SetDriveRates(10.0, 45.0));
    	//addSequential(new EnableDrivePIDCommand());
 
    	addSequential(new EnableDrivePIDCommand());
    	
    	/* First block to try */
    	/*
    	addSequential(new TurnAbsolute(0, 10));
    	addSequential(new DistanceDriveStraight(5.0,2.0, true));
    	addSequential(new DistanceDriveStraight(-5.0,-2.0, true));
    	addSequential(new DistanceDriveStraight(5.0,2.0, true));
    	addSequential(new DistanceDriveStraight(-5.0,-2.0, true));
    	addSequential(new DistanceDriveStraight(5.0,2.0, true));
    	addSequential(new DistanceDriveStraight(-5.0,-2.0, true));
    	/* */ 
   
    	/* Second block to try */
    	addSequential(new TurnAbsolute(0, 10));
    	addSequential(new DistanceDriveStraight(10.0,4.0, true));
    	addSequential(new TurnAbsolute(-180, 200));
    	addSequential(new DistanceDriveStraight(10.0,4.0, true));
    	addSequential(new TurnAbsolute(0, 200));
    	addSequential(new DistanceDriveStraight(10.0,4.0, true));
    	addSequential(new TurnAbsolute(-180, 200));
    	 //*/ 	
    	
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
