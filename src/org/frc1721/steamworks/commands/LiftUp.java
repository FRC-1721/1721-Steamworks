package org.frc1721.steamworks.commands;

import org.frc1721.steamworks.Robot;
import org.frc1721.steamworks.RobotMap;

import edu.wpi.first.wpilibj.command.Command;

public class LiftUp extends Command {
	
	protected boolean complete = false;
	
	public LiftUp() {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
		requires(Robot.lift);
    }

	// Called just before this Command runs the first time
	protected void initialize() {
		RobotMap.lLift.set(1.0);
	}

	// Called repeatedly when this Command is scheduled to run
	protected void execute() {
	}

	// Make this return true when this Command no longer needs to run execute()
	protected boolean isFinished() {
		if(Robot.topLimitSwitch.get())
			complete = true;
		return complete;
	}

	// Called once after isFinished returns true
	protected void end() {
		RobotMap.lLift.set(0.0);
	}

	// Called when another command which requires one or more of the same
	// subsystems is scheduled to run
	protected void interrupted() {
		complete = true;
	}
}
