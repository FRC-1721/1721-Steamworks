package org.frc1721.steamworks.commands;

import org.frc1721.steamworks.OI;
import org.frc1721.steamworks.Robot;
import org.frc1721.steamworks.subsystems.ShooterController;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class Shooter extends Command {
	protected boolean complete = false;

	public Shooter() {
		// Use requires() here to declare subsystem dependencies
		// eg. requires(chassis);
		requires(Robot.shooter);
	}

	// Called just before this Command runs the first time
	protected void initialize() {}

	// Called repeatedly when this Command is scheduled to run
	protected void execute() {
		ShooterController.jstick(OI.jOp);
	}

	// Make this return true when this Command no longer needs to run execute()
	protected boolean isFinished() {
		return complete;
	}

	// Called once after isFinished returns true
	protected void end() {}

	// Called when another command which requires one or more of the same
	// subsystems is scheduled to run
	protected void interrupted() {
		complete = true;
	}
}
